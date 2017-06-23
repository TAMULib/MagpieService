package edu.tamu.app.service.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

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

    public AbstractFedoraRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Document push(Document document) throws IOException {

        prepForPush();

        // create item container
        String itemContainerPath = createItemContainer(document.getName());

        File[] files = getFiles(document.getDocumentPath());

        pushFiles(document, itemContainerPath, files);

        updateMetadata(document, itemContainerPath);

        document.addPublishedLocation(new PublishedLocation(projectRepository, itemContainerPath));

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
        return buildRepoRestUrl() + File.separator + getContainerPath();
    }

    protected String buildRepoRestUrl() {
        return getRepoUrl() + File.separator + getRestPath();
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

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public DocumentRepo getDocumentRepo() {
        return documentRepo;
    }

    public void setDocumentRepo(DocumentRepo documentRepo) {
        this.documentRepo = documentRepo;
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

        HttpURLConnection getConnection = buildBasicFedoraConnection(itemContainerUrl);

        getConnection.setRequestMethod("GET");
        getConnection.setRequestProperty("Accept", "text/turtle");

        StringWriter writer = new StringWriter();

        BufferedReader in = new BufferedReader(new InputStreamReader(getConnection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            writer.append(inputLine);
        }

        in.close();
        writer.append(" ");

        for (MetadataFieldGroup group : document.getFields()) {
            for (MetadataFieldValue value : group.getValues()) {
                writer.append("<> " + group.getLabel().getName().replace('.', ':') + " \"" + value.getValue() + "\" . ");
            }
        }

        HttpURLConnection putConnection = buildBasicFedoraConnection(itemContainerUrl);
        putConnection.setRequestMethod("PUT");

        putConnection.setRequestProperty("CONTENT-TYPE", "text/turtle");
        putConnection.setDoOutput(true);

        OutputStream os = putConnection.getOutputStream();
        os.write(writer.toString().getBytes());
        os.close();

        if (putConnection.getResponseCode() != 204) {
            throw new IOException("Could not update metadata. Server responded with " + putConnection.getResponseCode());
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

    protected String confirmProjectContainerExists() throws IOException {
        String projectContainerPath = null;
        if (!resourceExists(buildContainerUrl())) {
            projectContainerPath = createContainer(buildRepoRestUrl(), getContainerPath());
        } else {
            projectContainerPath = getContainerPath().replace(getRepoUrl() + File.separator + getRestPath(), "");
        }
        return projectContainerPath;
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

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

    public String getRepoUrl() {
        return projectRepository.getSettingValues("repoUrl").get(0);
    }

    public String getRestPath() {
        return projectRepository.getSettingValues("restPath").get(0);
    }

    public String getContainerPath() {
        return projectRepository.getSettingValues("containerPath").get(0);
    }

    public String getUsername() {
        return projectRepository.getSettingValues("userName").get(0);
    }

    public String getPassword() {
        return projectRepository.getSettingValues("password").get(0);
    }

    abstract void prepForPush() throws IOException;

    abstract String createItemContainer(String slugName) throws IOException;

    abstract String createResource(String filePath, String resourceContainerPath, String slugName) throws IOException;

    abstract String createContainer(String containerUrl, String slugName) throws IOException;

}
