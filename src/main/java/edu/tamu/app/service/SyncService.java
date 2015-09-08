/* 
 * SyncService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.ProjectFieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

/** 
 * Sync Service. Synchronizes project database with projects folders.
 * 
 * @author
 *
 */
@Service
@PropertySource("classpath:/config/application.properties")
public class SyncService implements Runnable {
		
	private ProjectRepo projectRepo;
	
	private DocumentRepo documentRepo;
	
	private ProjectFieldProfileRepo projectFieldProfileRepo;
	
	private MetadataFieldRepo metadataFieldRepo;
	
	private MetadataFieldLabelRepo metadataFieldLabelRepo;	
	
	private Environment env;
	
	private ApplicationContext appContext;
	
	private SimpMessagingTemplate simpMessagingTemplate;
	
	private ExecutorService executorService;
	
	private ObjectMapper objectMapper;
	
	/**
	 * Default constructor.
	 * 
	 */
	public SyncService(){
		super();
	}
	
	public SyncService(ProjectRepo projectRepo,
					   DocumentRepo documentRepo,
					   ProjectFieldProfileRepo projectFieldProfileRepo,
					   MetadataFieldRepo metadataFieldRepo,
					   MetadataFieldLabelRepo metadataFieldLabelRepo,
					   Environment env,
					   ApplicationContext appContext,
					   SimpMessagingTemplate simpMessagingTemplate,
					   ExecutorService executorService,
					   ObjectMapper objectMapper) {
		super();
		this.projectRepo = projectRepo;
		this.documentRepo = documentRepo;
		this.metadataFieldRepo = metadataFieldRepo;
		this.projectFieldProfileRepo = projectFieldProfileRepo;
		this.metadataFieldLabelRepo = metadataFieldLabelRepo;
		this.env = env;
		this.appContext = appContext;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.executorService = executorService;
		this.objectMapper = objectMapper;
	}

	/**
	 * SyncService runnable.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {		
		System.out.println("Running Sync Service");
		
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/metadata.json")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String, Object> projectMap = null;
		
		try {
			projectMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		String host = env.getProperty("app.host");
		String mount = env.getProperty("app.mount");
		
		String directory = null;
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath();			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Path> projects = fileList(directory);
        
        for(Path projectPath : projects) {
        	
        	System.out.println("Watching: " + projectPath.getFileName().toString());
        	
        	Project project = projectRepo.create(projectPath.getFileName().toString());
        	
        	List<Path> documents = fileList(projectPath.toString());
        	
        	List<Object> profileObjList = (List<Object>) projectMap.get(projectPath.getFileName().toString());
        	
        	List<MetadataFieldGroup> fields = new ArrayList<MetadataFieldGroup>();
        	
        	executorService.submit(new WatcherService(projectRepo,
        											  documentRepo,
        											  projectFieldProfileRepo,
        											  metadataFieldRepo,
        											  metadataFieldLabelRepo,
					   								  env,
					   								  appContext,
					   								  simpMessagingTemplate,
					   								  executorService,
					   								  objectMapper,
					   								  projectPath.getFileName().toString()));
        	
        	if(profileObjList == null) profileObjList = (List<Object>) projectMap.get("default");
        	
        	for(Object metadata : profileObjList) {
				
				Map<String, Object> mMap = (Map<String, Object>) metadata;
				
				MetadataFieldLabel label = metadataFieldLabelRepo.create((String) mMap.get("label"));
				
				ProjectLabelProfile profile = projectFieldProfileRepo.create(label,
																			 project,
																			 (String) mMap.get("gloss"), 
																			 (Boolean) mMap.get("repeatable"), 
																			 (Boolean) mMap.get("readOnly"),
																			 (Boolean) mMap.get("hidden"),
																			 (Boolean) mMap.get("required"),
																			 InputType.valueOf((String) mMap.get("inputType")),
																			 (String) mMap.get("default"));
				
				
				label.addProfile(profile);
				metadataFieldLabelRepo.save(label);
				
				fields.add(new MetadataFieldGroup(label));
								
				project.addProfile(profile);
				projectRepo.save(project);
			}
        	
        	for(Path documentPath : documents) {
        		
        		System.out.println("Adding: " + documentPath.getFileName().toString());
        		
    			String pdfPath = "/mnt/projects/"+projectPath.getFileName().toString()+"/"+documentPath.getFileName().toString()+"/"+documentPath.getFileName().toString()+".pdf";
				String txtPath = "/mnt/projects/"+projectPath.getFileName().toString()+"/"+documentPath.getFileName().toString()+"/"+documentPath.getFileName().toString()+".txt";
        		String pdfUri = host+pdfPath;
        		String txtUri = host+txtPath;
        		
        		Document document = documentRepo.create(project, documentPath.getFileName().toString(), txtUri, pdfUri, txtPath, pdfPath, "Open");
        		
        		fields.forEach(field -> {
					document.addField(metadataFieldRepo.create(document, field.getLabel()));
				});
        		
        		project.addDocument(documentRepo.save(document));
        		        		
				Map<String, Object> docMap = new HashMap<String, Object>();				
				docMap.put("document", document);				
				docMap.put("isNew", "true");
				
				try {
					simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse("success", docMap, new RequestId("0")));
	        	}
	        	catch(Exception e) {
	        		System.out.println("CRASHED WHILE TRYING TO SEND DOCUMENT!!!");
	        		e.printStackTrace();
	        		System.exit(-1);
	        	}
				
        	}
        	
        	try {
        		projectRepo.save(project);
        	}
        	catch(Exception e) {
        		System.out.println("CRASHED WHILE TRYING TO SAVE PROJECT!!!");
        		e.printStackTrace();
        		System.exit(-1);
        	}
        	
        }
		
	}
	
	/**
	 * Retrieves a list of files in a directory.
	 * 
	 * @param 		directory		String
	 * 
	 * @return		List<Path>
	 * 
	 */
	public static List<Path> fileList(String directory) {
        List<Path> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path);            
            }
        } catch (IOException ex) {}
        return fileNames;
    }

}
