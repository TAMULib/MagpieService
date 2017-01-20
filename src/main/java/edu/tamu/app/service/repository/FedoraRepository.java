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

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.DocumentRepo;

public class FedoraRepository implements Repository {
    
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
    
    public FedoraRepository(String repoUrl, String restPath, String containerPath,String username, String password) {
    	setRepoUrl(repoUrl);
    	setRestPath(restPath);
    	setContainerPath(containerPath);
    	setUsername(username);
    	setPassword(password);
    }

	@Override
	public Document push(Document document) throws IOException {
		
		setContainerPath(confirmProjectContainerExists());
        
		//create item container
		String itemContainerPath = createContainer(getContainerPath(), document.getName());
				
		File directory = resourceLoader.getResource("classpath:static" + document.getDocumentPath()).getFile();
		File[] files = directory.listFiles();

		String itemPath = null;
		for (File file : files) {
		    if (file.isFile()) {
		    	itemPath = createResource(document.getDocumentPath()+File.separator+file.getName(), itemContainerPath, file.getName());
		    }
		}
		updateMetadata(document,itemContainerPath);		
		document.setPublishedUriString(itemPath);
		document.setStatus("Published");
		document = documentRepo.save(document);
	
		return document;
	}
	
	/**
	 * Updates a Fedora Resource container's metadata
	 * @param document
	 * @param itemContainerPath
	 * @throws IOException
	 */
	
	private void updateMetadata(Document document, String itemContainerPath) throws IOException {
		String containerURL = getRepoUrl()+"/"+getRestPath()+"/"+getContainerPath()+"/"+itemContainerPath+"/";
		
		HttpURLConnection getConnection = buildBasicFedoraConnection(containerURL);

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

		HttpURLConnection putConnection = buildBasicFedoraConnection(containerURL);
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
	
	private String createResource(String filePath, String itemContainerPath, String slug) throws IOException {
				
		File file = resourceLoader.getResource("classpath:static" + filePath).getFile();
        FileInputStream fileStrm = new FileInputStream(file);
        byte[] fileBytes = IOUtils.toByteArray(fileStrm);

        HttpURLConnection connection = buildFedoraConnection(getContainerPath()+"/"+itemContainerPath, "POST");
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
	
	private String confirmProjectContainerExists() throws IOException {
		String projectContainerPath = null;
		if(buildFedoraConnection(getContainerPath(), "GET").getResponseCode() == 404) {
			projectContainerPath = createContainer("", getContainerPath());
		} else {
			projectContainerPath = getContainerPath().replace(getRepoUrl()+"/"+getRestPath(), "");
		}
		return projectContainerPath;
	}
	
	private String createContainer(String containerName, String slugName) throws IOException {
				
		HttpURLConnection connection = buildFedoraConnection(containerName, "POST");
		connection.setRequestProperty("Accept", null);
		if(slugName != null) connection.setRequestProperty("slug", slugName);
		
		int responseCode = connection.getResponseCode();
				
		if(responseCode != 201) {
			throw new IOException("Could not create container. Server responded with " + responseCode);
		}
				
		StringWriter writer = new StringWriter();
		IOUtils.copy(connection.getInputStream(), writer);
		
		return writer.toString().replace(getRepoUrl()+"/"+getRestPath()+"/"+getContainerPath()+"/", "");
			
	}

	private HttpURLConnection buildBasicFedoraConnection(String path) throws IOException {
		
		URL restUrl = new URL(path);
		
		HttpURLConnection connection = (HttpURLConnection) restUrl.openConnection();
		
        String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes());
        connection.setRequestProperty("Authorization", "Basic "+encoded);
        
        return connection;
		
	}
	
	private HttpURLConnection buildFedoraConnection(String path, String method) throws IOException {
		
		URL restUrl = new URL(getRepoUrl()+"/"+getRestPath()+"/"+path);
		
		HttpURLConnection connection = (HttpURLConnection) restUrl.openConnection();
		
		connection.setRequestMethod(method);		
        connection.setRequestProperty("Accept", "application/ld+json");
        		
        String encoded = Base64.getEncoder().encodeToString((username+":"+password).getBytes());
        connection.setRequestProperty("Authorization", "Basic "+encoded);
        
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
