package edu.tamu.app.service.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.DocumentRepo;

public class FedoraRepository extends AbstractFedoraRepository {
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private DocumentRepo documentRepo;
    
    @Autowired
    ConfigurableMimeFileTypeMap configurableMimeFileTypeMap;
    
    
    @Value("${app.mount}")
    private String mount;
    
    private String repoUrl;
    
    private String restPath;
    
    private String containerPath;
    
    private String username;
    
    private String password;
    
    protected static final Logger logger = Logger.getLogger(FedoraRepository.class);

    
    public FedoraRepository(String repoUrl, String restPath, String containerPath,String username, String password) {
    	setRepoUrl(repoUrl);
    	setRestPath(restPath);
    	setContainerPath(containerPath);
    	setUsername(username);
    	setPassword(password);
    }
    
	@Override
	public Document push(Document document) throws IOException {
		
		prepForPush();
		
		//create item container
		String itemContainerPath = createItemContainer(document.getName());
				
		File[] files = getFiles(document.getDocumentPath());
		
		String itemPath = pushFiles(document, itemContainerPath, files);

		updateMetadata(document,itemContainerPath);		
		document.setPublishedUriString(itemPath);
		document.setStatus("Published");
		document = documentRepo.save(document);
	
		return document;
	}
	
    protected File[] getFiles(String directoryPath) throws IOException {
		File directory = resourceLoader.getResource("classpath:static" + directoryPath).getFile();
		return directory.listFiles();
    }
    
    protected String pushFiles(Document document, String itemContainerPath, File[] files) throws IOException {
		String itemPath = null;
		for (File file : files) {
		    if (file.isFile()) {
		    	itemPath = createResource(document.getDocumentPath()+File.separator+file.getName(),itemContainerPath, file.getName());
		    }
		}
		return itemPath;
    }
	
	protected void prepForPush() throws IOException {
		confirmProjectContainerExists();
	}
	
	protected String buildContainerUrl() {
		return buildRepoRestUrl()+File.pathSeparator+getContainerPath();
	}
	
	protected String buildRepoRestUrl() {
		return getRepoUrl()+File.pathSeparator+getRestPath();
	}
	/**
	 * Updates a Fedora Resource container's metadata
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
				writer.append("<> "+group.getLabel().getName().replace('.', ':')+" \""+value.getValue()+"\" . ");
			}			
		}

		HttpURLConnection putConnection = buildBasicFedoraConnection(itemContainerUrl);
		putConnection.setRequestMethod("PUT");		
		
		putConnection.setRequestProperty("CONTENT-TYPE", "text/turtle");
		putConnection.setDoOutput(true);
		
		OutputStream os = putConnection.getOutputStream();
		os.write(writer.toString().getBytes());
		os.close();
		
		if(putConnection.getResponseCode() != 204) {
			throw new IOException("Could not update metadata. Server responded with " + putConnection.getResponseCode());
		}
		
	}
	
	protected String createResource(String filePath, String itemContainerPath, String slug) throws IOException {
				
		File file = resourceLoader.getResource("classpath:static" + filePath).getFile();
        FileInputStream fileStrm = new FileInputStream(file);
        byte[] fileBytes = IOUtils.toByteArray(fileStrm);
        HttpURLConnection connection = buildFedoraConnection(itemContainerPath, "POST");
		connection.setRequestProperty("CONTENT-TYPE", configurableMimeFileTypeMap.getContentType(file));
		connection.setRequestProperty("Accept", null);
		
		if(slug != null) connection.setRequestProperty("slug", slug);

		connection.setDoOutput(true);
		
        OutputStream os = connection.getOutputStream();
        os.write(fileBytes);
        os.close();
                
        StringWriter writer = new StringWriter();
		IOUtils.copy(connection.getInputStream(), writer);
		return connection.getHeaderField("Location");
	}
	
	protected String confirmProjectContainerExists() throws IOException {
		String projectContainerPath = null;
		if(!resourceExists(buildContainerUrl())) {
			projectContainerPath = createContainer(buildRepoRestUrl(), getContainerPath());
		} else {
			projectContainerPath = getContainerPath().replace(getRepoUrl()+File.pathSeparator+getRestPath(), "");
		}
		return projectContainerPath;
	}
	
	protected boolean resourceExists(String uri) throws IOException {
		HttpURLConnection connection = getResource(uri,null);
		int responseCode = connection.getResponseCode();
		logger.debug("Checking Fedora for existence of <"+uri+">: "+responseCode);
		if (responseCode == 200 || responseCode == 304) {
			logger.debug("<"+uri+"> exists");
			return true;
		}
		logger.debug("<"+uri+"> does not exist");
		return false;
	}
	
	protected HttpURLConnection getResource(String uri,Map<String,String> requestProperties) throws IOException {
		HttpURLConnection connection = buildBasicFedoraConnection(uri);

		connection.setRequestMethod("GET");		
	
		if (requestProperties != null) {
			requestProperties.forEach((k,v) -> {
				connection.addRequestProperty(k, v);
			});
		}		
		return connection;
	}

	protected String createItemContainer(String slugName) throws IOException {
		return createContainer(buildContainerUrl(), slugName);
	}

	
	protected String createContainer(String containerUrl, String slugName) throws IOException {
		HttpURLConnection connection = buildFedoraConnection(containerUrl, "POST");
		connection.setRequestProperty("Accept", null);
		if(slugName != null) connection.setRequestProperty("slug", slugName);
		
		int responseCode = connection.getResponseCode();
				
		if(responseCode != 201) {
			throw new IOException("Could not create container. Server responded with " + responseCode);
		}
				
		StringWriter writer = new StringWriter();
		IOUtils.copy(connection.getInputStream(), writer);
		
		return connection.getHeaderField("Location");
	}

	protected HttpURLConnection buildBasicFedoraConnection(String path) throws IOException {
		
		URL restUrl = new URL(path);
		
		HttpURLConnection connection = (HttpURLConnection) restUrl.openConnection();
		
        String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes());
        connection.setRequestProperty("Authorization", "Basic "+encoded);
        
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

	public String getMount() {
		return mount;
	}

	public void setMount(String mount) {
		this.mount = mount;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public String getRestPath() {
		return restPath;
	}

	public void setRestPath(String restPath) {
		this.restPath = restPath;
	}

	public String getContainerPath() {
		return containerPath;
	}

	public void setContainerPath(String containerId) {
		this.containerPath = containerId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
