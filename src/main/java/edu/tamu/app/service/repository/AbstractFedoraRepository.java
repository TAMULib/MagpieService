package edu.tamu.app.service.repository;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.PublishedLocation;
import edu.tamu.app.model.repo.DocumentRepo;

public abstract class AbstractFedoraRepository implements Repository {

    protected static final Logger logger = Logger.getLogger(AbstractFedoraRepository.class);

    @Value("${app.mount}")
    private String mount;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private DocumentRepo documentRepo;

    private ProjectRepository projectRepository;

    private Optional<String> transactionalUrl;

    public AbstractFedoraRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        this.transactionalUrl = Optional.empty();
    }

    public Document push(Document document) throws IOException {

        try {

            prepForPush();

            String itemContainerPath = createItemContainer(document.getName());

            File[] files = getFiles(document.getDocumentPath());

            pushFiles(document, itemContainerPath, files);

            updateMetadata(document, itemContainerPath);

            document.addPublishedLocation(new PublishedLocation(projectRepository, getUrlWithoutTransaction(itemContainerPath)));

            commmitTransaction();

        } catch (IOException ioe) {
            rollbackTransaction();
            throw ioe;
        }

        document.setStatus("Published");

        return documentRepo.save(document);
    }

    protected String pushFiles(Document document, String itemContainerPath, File[] files) throws IOException {
        String itemPath = null;
        for (File file : files) {
            if (file.isFile() && !file.isHidden()) {
                itemPath = createResource(document.getDocumentPath() + File.separator + file.getName(), itemContainerPath, file.getName());
            }
        }
        return itemPath;
    }

    protected String buildContainerUrl() {
        return String.join("/", buildRepoRestUrl(), getContainerPath());
    }

    protected String buildRepoRestUrl() {
        if (!transactionalUrl.isPresent()) {
            throw new RuntimeException("No transaction in which to process request!");
        }
        return transactionalUrl.get();
    }

    protected String getUrlWithoutTransaction(String url) {
        return url.replaceAll(transactionalUrl.get(), String.join("/", getRepoUrl(), getRestPath()));
    }

    protected String getEncodedBasicAuthorization() {
        String encoded = Base64.getEncoder().encodeToString((getUsername() + ":" + getPassword()).getBytes());
        return "Basic " + encoded;
    }

    protected HttpURLConnection buildBasicFedoraConnection(String path) throws IOException {
        URL restUrl = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) restUrl.openConnection();
        if (getUsername() != null && !getUsername().isEmpty() && getPassword() != null && !getPassword().isEmpty()) {
            connection.setRequestProperty("Authorization", getEncodedBasicAuthorization());
        }
        return connection;

    }

    protected HttpURLConnection buildFedoraConnection(String path, String method) throws IOException {
        HttpURLConnection connection = buildBasicFedoraConnection(path);
        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", "application/ld+json");
        return connection;

    }

    protected File[] getFiles(String directoryPath) throws IOException {
        File directory = resourceLoader.getResource("classpath:static" + directoryPath).getFile();
        return directory.listFiles();
    }

    /**
     * Updates a Fedora Resource container's metadata
     * 
     * @param document
     * @param itemContainerPath
     * @throws IOException
     */
    private void updateMetadata(Document document, String itemContainerUrl) throws IOException {
        String updateQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>" + "INSERT {";
        for (MetadataFieldGroup group : document.getFields()) {
            for (MetadataFieldValue value : group.getValues()) {
                updateQuery += "<> " + group.getLabel().getName().replace('.', ':') + " \"" + value.getValue() + "\" . ";
            }
        }
        updateQuery += "} WHERE { }";
        executeSparqlUpdate(itemContainerUrl, updateQuery);
    }

    protected void executeSparqlUpdate(String uri, String sparqlQuery) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPatch httpPatch;
        try {
            httpPatch = new HttpPatch(new URI(uri));

            logger.debug("**** PATCHING SPARQL UPDATE ****");
            logger.debug(sparqlQuery);
            StringEntity data = new StringEntity(sparqlQuery);

            data.setContentType("application/sparql-update");

            httpPatch.setEntity(data);

            httpPatch.addHeader("Authorization", getEncodedBasicAuthorization());
            httpPatch.addHeader("CONTENT-TYPE", "application/sparql-update");
            CloseableHttpResponse response = httpClient.execute(httpPatch);

            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 204) {
                throw new IOException("Could not complete PATCH request. Server responded with " + responseCode);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected String confirmProjectContainerExists() throws IOException {
        String projectContainerPath = null;
        if (!resourceExists(buildContainerUrl())) {
            projectContainerPath = createContainer(buildRepoRestUrl(), getContainerPath());
        } else {
            projectContainerPath = getContainerPath().replace(String.join("/", getRepoUrl(), getRestPath()), "");
        }
        return projectContainerPath;
    }

    protected boolean resourceExists(String uri) throws IOException {
        HttpURLConnection connection = getResource(uri, null);
        int responseCode = connection.getResponseCode();
        logger.debug("Checking Fedora for existence of <" + uri + ">: " + responseCode);
        if (responseCode == 200 || responseCode == 304) {
            logger.debug("<" + uri + "> exists");
            return true;
        }
        logger.debug("<" + uri + "> does not exist");
        return false;
    }

    protected HttpURLConnection getResource(String uri, Map<String, String> requestProperties) throws IOException {
        HttpURLConnection connection = buildBasicFedoraConnection(uri);
        connection.setRequestMethod("GET");
        if (requestProperties != null) {
            requestProperties.forEach((k, v) -> {
                connection.addRequestProperty(k, v);
            });
        }
        return connection;
    }

    protected void startTransaction() throws IOException {
        HttpURLConnection connection = buildFedoraConnection(String.join("/", getRepoUrl(), getRestPath(), "fcr:tx"), "POST");
        transactionalUrl = Optional.of(connection.getHeaderField("Location"));
    }

    protected void commmitTransaction() throws IOException {
        HttpURLConnection connection = buildFedoraConnection(String.join("/", transactionalUrl.get(), "fcr:tx", "fcr:commit"), "POST");
        logger.info("Transaction commit status: " + connection.getResponseCode());
        transactionalUrl = Optional.empty();
    }

    protected void rollbackTransaction() throws IOException {
        HttpURLConnection connection = buildFedoraConnection(String.join("/", transactionalUrl.get(), "fcr:tx", "rollback"), "POST");
        logger.info("Transaction rollback status: " + connection.getResponseCode());
        transactionalUrl = Optional.empty();
    }

    protected URL buildTransactionalUrl(String path) throws MalformedURLException {
        return new URL(path);
    }

    protected String getRepoUrl() {
        return projectRepository.getSettingValues("repoUrl").get(0);
    }

    protected String getRestPath() {
        return projectRepository.getSettingValues("restPath").get(0);
    }

    protected String getContainerPath() {
        return projectRepository.getSettingValues("containerPath").get(0);
    }

    protected String getUsername() {
        return projectRepository.getSettingValues("userName").get(0);
    }

    protected String getPassword() {
        return projectRepository.getSettingValues("password").get(0);
    }

    protected ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    protected Optional<String> getTransactionalUrl() {
        return transactionalUrl;
    }

    abstract void prepForPush() throws IOException;

    abstract String createItemContainer(String slugName) throws IOException;

    abstract String createResource(String filePath, String resourceContainerPath, String slugName) throws IOException;

    abstract String createContainer(String containerUrl, String slugName) throws IOException;

}
