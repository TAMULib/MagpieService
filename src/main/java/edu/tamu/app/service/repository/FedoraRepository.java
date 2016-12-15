package edu.tamu.app.service.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
		
		System.out.println(getContainerPath());
		System.out.println(itemContainerPath);
				
		File directory = resourceLoader.getResource("classpath:static" + document.getDocumentPath()).getFile();
		File[] files = directory.listFiles();
		
		for (File file : files) {
		    if (file.isFile()) {
		    	createResource(document.getDocumentPath()+File.separator+file.getName(), itemContainerPath, null);
		    }
		}
		
		document.setPublishedUriString(getRepoUrl()+"/"+getRestPath()+"/"+getContainerPath()+"/"+itemContainerPath);
		document.setStatus("Published");
		document = documentRepo.save(document);
		
		return document;
	}
	
	private void createResource(String filePath, String itemContainerPath, String slug) throws IOException {
				
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
        
        int responseCode = connection.getResponseCode();
        
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
		
		System.out.println("Response Message: "+connection.getResponseMessage());
		
		if(responseCode != 201) throw new IOException("Could not create container. Server responded with " + responseCode);
				
		StringWriter writer = new StringWriter();
		IOUtils.copy(connection.getInputStream(), writer);
		
		return writer.toString().replace(getRepoUrl()+"/"+getRestPath()+"/"+getContainerPath()+"/", "");
			
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
