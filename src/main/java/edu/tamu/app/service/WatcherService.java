/* 
 * WatcherService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.ProjectFieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

/** 
 * Watcher Service. Watches projects folder and inserts created documents into database.
 * 
 * @author
 *
 */
@Component
@Service
@PropertySource("classpath:/config/application.properties")
public class WatcherService implements Runnable {
	
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
	
	private String folder;
	
	/**
	 * Default constructor.
	 * 
	 */
	public WatcherService(){
		super();
	}
	
	public WatcherService(ProjectRepo projectRepo,
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
		this.projectFieldProfileRepo = projectFieldProfileRepo;
		this.metadataFieldRepo = metadataFieldRepo;
		this.metadataFieldLabelRepo = metadataFieldLabelRepo;
		this.env = env;
		this.appContext = appContext;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.executorService = executorService;
		this.objectMapper = objectMapper;
	}
	
	public WatcherService(ProjectRepo projectRepo,
						  DocumentRepo documentRepo,
						  ProjectFieldProfileRepo projectFieldProfileRepo,
						  MetadataFieldRepo metadataFieldRepo,
						  MetadataFieldLabelRepo metadataFieldLabelRepo,
						  Environment env,
						  ApplicationContext appContext,
						  SimpMessagingTemplate simpMessagingTemplate,
						  ExecutorService executorService,
						  ObjectMapper objectMapper,
						  String folder) {
		super();
		this.projectRepo = projectRepo;
		this.documentRepo = documentRepo;
		this.projectFieldProfileRepo = projectFieldProfileRepo;
		this.metadataFieldRepo = metadataFieldRepo;
		this.metadataFieldLabelRepo = metadataFieldLabelRepo;
		this.env = env;
		this.appContext = appContext;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.executorService = executorService;
		this.objectMapper = objectMapper;
		this.folder = folder;
	}
	
	/**
	 * WatcherService runnable.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/metadata.json")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<String, Object> projectMap = null;
		
		try {
			projectMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		List<MetadataFieldGroup> fields = new ArrayList<MetadataFieldGroup>();
		
		String host = env.getProperty("app.host");
		String mount = env.getProperty("app.mount");
		
		String directory = "";
		try {
			directory = appContext.getResource("classpath:static/mnt").getFile().getAbsolutePath() + "/" + folder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Project project = projectRepo.findByName(folder);
		
		
		if(!folder.equals("projects")) {
			
			try {
				directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/" + folder;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<Object> profileObjList = (List<Object>) projectMap.get(folder);
			
			if(profileObjList == null) profileObjList = (List<Object>) projectMap.get("default");
			
			
    		if(project == null) {
    			project = projectRepo.create(folder);
    		}
    		
			
			for(Object metadata : profileObjList) {
				
				Map<String, Object> mMap = (Map<String, Object>) metadata;
				
				MetadataFieldLabel label = metadataFieldLabelRepo.create((String) mMap.get("label"));
				
				ProjectLabelProfile profile = projectFieldProfileRepo.create(label,
																	  		 projectRepo.findByName(folder),
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
		}
		
		try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = FileSystems.getDefault().getPath(directory, "");
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
             
            System.out.println("Watch Service registered for dir: " + dir.getFileName());
             
            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    
                    String docString = fileName.toString();
                    
                    System.out.println(kind.name() + ": " + fileName);

                    if (kind == ENTRY_CREATE) {
                    	if(folder.equals("projects")) {
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
	   								  								  docString));
                    	}
                    	else {
                    		
	                    	if((documentRepo.findByName(docString) == null)) {
	                    		
	        					String pdfPath = "/mnt/projects/"+folder+"/"+docString+"/"+docString+".pdf";
	            				String txtPath = "/mnt/projects/"+folder+"/"+docString+"/"+docString+".txt";
	            				
	                    		String pdfUri = host+pdfPath;
	                    		String txtUri = host+txtPath;
	                         		
	        					Document document = documentRepo.create(project, docString, txtUri, pdfUri, txtPath, pdfPath, "Open");
	        					
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
	        					
	        		        	projectRepo.save(project);
	                    	}
	                    	
                    	}
                    	
                    }                    
                    else if(kind == ENTRY_MODIFY) {
                    	
                    }
                    else if(kind == ENTRY_DELETE) {
                    	
                    }
                    else {
                    	
                    }
                    
                }
                 
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
             
        } catch (IOException ex) {
            System.err.println(ex);
        }
		
	}
	
}
