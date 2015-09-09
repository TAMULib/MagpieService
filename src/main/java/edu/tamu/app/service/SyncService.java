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
import java.lang.reflect.Field;
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
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.response.marc.FlatMARC;

/** 
 * Sync Service. Synchronizes project database with projects folders.
 * 
 * @author
 *
 */
@Service
@PropertySource("classpath:/config/application.properties")
public class SyncService implements Runnable {
	
	private VoyagerService voyagerService; 
		
	private ProjectRepo projectRepo;
	
	private DocumentRepo documentRepo;
	
	private ProjectLabelProfileRepo projectLabelProfileRepo;
	
	private MetadataFieldGroupRepo metadataFieldRepo;
	
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
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
	
	public SyncService(VoyagerService voyagerService,
					   ProjectRepo projectRepo,
					   DocumentRepo documentRepo,
					   ProjectLabelProfileRepo projectLabelProfileRepo,
					   MetadataFieldGroupRepo metadataFieldRepo,
					   MetadataFieldLabelRepo metadataFieldLabelRepo,
					   MetadataFieldValueRepo metadataFieldValueRepo,
					   Environment env,
					   ApplicationContext appContext,
					   SimpMessagingTemplate simpMessagingTemplate,
					   ExecutorService executorService,
					   ObjectMapper objectMapper) {
		super();
		this.voyagerService = voyagerService;
		this.projectRepo = projectRepo;
		this.documentRepo = documentRepo;
		this.metadataFieldRepo = metadataFieldRepo;
		this.projectLabelProfileRepo = projectLabelProfileRepo;
		this.metadataFieldLabelRepo = metadataFieldLabelRepo;
		this.metadataFieldValueRepo = metadataFieldValueRepo;
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
			if(objectMapper == null) {
				System.out.println("NULL OBJECT MAPPER");
			}
			projectMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		
		String host = env.getProperty("app.host");
		String mount = env.getProperty("app.mount");
		
		String directory = null;
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/projects/";			
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
        	
        	executorService.submit(new WatcherService(voyagerService,
        											  projectRepo,
        											  documentRepo,
        											  projectLabelProfileRepo,
        											  metadataFieldRepo,
        											  metadataFieldLabelRepo,
        											  metadataFieldValueRepo,
					   								  env,
					   								  appContext,
					   								  simpMessagingTemplate,
					   								  executorService,
					   								  objectMapper,
					   								  projectPath.getFileName().toString()));
        	
        	if(profileObjList == null) profileObjList = (List<Object>) projectMap.get("default");
        	
        	for(Object metadata : profileObjList) {
				
				Map<String, Object> mMap = (Map<String, Object>) metadata;
				
				ProjectLabelProfile profile = projectLabelProfileRepo.create(project,
																			 (String) mMap.get("gloss"), 
																			 (Boolean) mMap.get("repeatable"), 
																			 (Boolean) mMap.get("readOnly"),
																			 (Boolean) mMap.get("hidden"),
																			 (Boolean) mMap.get("required"),
																			 InputType.valueOf((String) mMap.get("inputType")),
																			 (String) mMap.get("default"));
				
				MetadataFieldLabel label = metadataFieldLabelRepo.create((String) mMap.get("label"), profile);
				
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
        		
        		
        		FlatMARC flatMarc = null;
				try {
					flatMarc = new FlatMARC(voyagerService.getMARC(document.getName()));
				} catch (Exception e1) {
					System.out.println("ERROR WHILE TRYING TO RETRIEVE MARC RECORD!!!");
					e1.printStackTrace();
				}
				
				Field[] marcFields = FlatMARC.class.getDeclaredFields();
				
				Map<String, List<String>> metadataMap = new HashMap<String, List<String>>();
				
				for (Field field : marcFields) {
					field.setAccessible(true);
		            List<String> marcList = new ArrayList<String>();
		            if(field.getGenericType().toString().equals("java.util.List<java.lang.String>")) {
		            	try {
							for(String string : (List<String>) field.get(flatMarc)) {
								marcList.add(string);
							}
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
		            }
		            else {
		            	try {
							marcList.add(field.get(flatMarc).toString());
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
		            }
		            
		            metadataMap.put(field.getName().replace('_','.'), marcList);
		        }
				
				document.getFields().forEach(field -> {
					List<String> values = metadataMap.get(field.getLabel().getName());
					if(values != null) {
						values.forEach(value -> {
							field.addValue(metadataFieldValueRepo.create(value, field));
						});
					}
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
