package edu.tamu.app.service.repository;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.PublishedLocation;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ResourceRepo;

public abstract class AbstractFedoraRepository implements Repository {

    protected static final Logger logger = Logger.getLogger(AbstractFedoraRepository.class);

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private ConfigurableMimeFileTypeMap configurableMimeFileTypeMap;

    private ProjectRepository projectRepository;

    public AbstractFedoraRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Document push(Document document) throws IOException {

        final String tid = prepForPush();

        try {

            String itemContainerPath = createItemContainer(document.getName(), tid);

            File[] files = getFiles(document);

            pushFiles(document, itemContainerPath, files);

            updateMetadata(document, itemContainerPath);

            document.addPublishedLocation(new PublishedLocation(projectRepository, getUrlWithoutTransaction(itemContainerPath, tid)));

            commmitTransaction(tid);

        } catch (IOException ioe) {
            rollbackTransaction(tid);
            ioe.printStackTrace();
            throw ioe;
        }

        document.setStatus("Published");

        return documentRepo.update(document);
    }

    protected void confirmProjectContainerExists(String tid) throws IOException {
        if (!resourceExists(buildTransactionalContainerUrl(tid))) {
            createContainer(String.join("/", getRepoUrl(), getRestPath(), tid), getContainerPath());
        }
    }

    protected String pushFiles(Document document, String itemContainerPath, File[] files) throws IOException {
        String itemPath = null;
        for (File file : files) {
            if (file.isFile() && !file.isHidden()) {
                itemPath = createResource(ASSETS_PATH + File.separator + document.getPath() + "/" + file.getName(), itemContainerPath, file.getName());
            }
        }
        return itemPath;
    }

    protected String buildTransactionalContainerUrl(final String tid) {
        String transactionalContainerUrl = String.join("/", getRepoUrl(), getRestPath(), tid, getContainerPath());
        logger.debug("Transactional container url: " + transactionalContainerUrl);
        return transactionalContainerUrl;
    }

    protected String buildTransactionaUrl(final String tid) {
        String transactionalUrl = String.join("/", getRepoUrl(), getRestPath(), tid);
        logger.debug("Transactional container url: " + transactionalUrl);
        return transactionalUrl;
    }

    protected String getUrlWithoutTransaction(String url, final String tid) {
        return url.replace("/" + tid, "");
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

    protected File[] getFiles(Document document) {
        List<Resource> resources = resourceRepo.findAllByDocumentProjectNameAndDocumentName(document.getProject().getName(), document.getName());
        File[] files = new File[resources.size()];
        int i = 0;
        for (Resource resource : resources) {
            files[i++] = new File(ASSETS_PATH + File.separator + resource.getPath());
        }
        return files;
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
        String cleanValue = null;
        for (MetadataFieldGroup group : document.getFields()) {
            for (MetadataFieldValue value : group.getValues()) {
                cleanValue = StringEscapeUtils.escapeJava(value.getValue());
                if (cleanValue.length() > 0)
                    updateQuery += "<> " + group.getLabel().getName().replace('.', ':') + " \"" + cleanValue + "\" . ";
            }
        }
        updateQuery += "} WHERE { }";
        executeSparqlUpdate(itemContainerUrl, updateQuery);
    }

    protected String createResource(String filePath, String itemContainerPath, String slug) throws IOException {

        File file = new File(filePath);
        FileInputStream fileStrm = new FileInputStream(file);
        byte[] fileBytes = IOUtils.toByteArray(fileStrm);
        HttpURLConnection connection = buildFedoraConnection(itemContainerPath, "POST");
        connection.setRequestProperty("CONTENT-TYPE", configurableMimeFileTypeMap.getContentType(file));
        connection.setRequestProperty("Accept", "*/*");

        if (slug != null) {
            connection.setRequestProperty("slug", slug);
        }

        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        os.write(fileBytes);
        os.close();

        return connection.getHeaderField("Location");
    }

    protected void executeSparqlUpdate(String uri, String sparqlQuery) throws ClientProtocolException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPatch httpPatch;
        try {
            httpPatch = new HttpPatch(new URI(uri));

            logger.debug("**** PATCHING SPARQL UPDATE at URL " + uri + " ****");
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

    protected String startTransaction() throws IOException {
        HttpURLConnection connection = buildFedoraConnection(String.join("/", getRepoUrl(), getRestPath(), "fcr:tx"), "POST");

        for (String header : connection.getHeaderFields().keySet()) {
            logger.debug("HTTP connection to open Fedora transaction got header \"" + header + "\" with value \"" + connection.getHeaderFields().get(header) + "\".");
        }

        String transactionalUrl = connection.getHeaderField("Location");

        return transactionalUrl.substring(transactionalUrl.lastIndexOf("/") + 1);
    }

    protected void commmitTransaction(String tid) throws IOException {
        String commitUrlString = String.join("/", buildTransactionaUrl(tid), "fcr:tx", "fcr:commit");
        logger.debug("Commit URL: " + commitUrlString);
        HttpURLConnection connection = buildFedoraConnection(commitUrlString, "POST");
        logger.info("Transaction commit status: " + connection.getResponseCode());
    }

    protected void rollbackTransaction(String tid) throws IOException {
        String rollbackUrlString = String.join("/", buildTransactionaUrl(tid), "fcr:tx", "fcr:rollback");
        logger.debug("Rollback URL: " + rollbackUrlString);
        HttpURLConnection connection = buildFedoraConnection(rollbackUrlString, "POST");
        logger.info("Transaction rollback status: " + connection.getResponseCode());
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

    abstract String prepForPush() throws IOException;

    abstract String createItemContainer(String slugName, final String tid) throws IOException;

    abstract String createContainer(String containerUrl, String slugName) throws IOException;

}
