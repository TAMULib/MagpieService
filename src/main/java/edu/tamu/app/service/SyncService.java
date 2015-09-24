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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
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
public class SyncService implements Runnable {
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private VoyagerService voyagerService; 
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ExecutorService executorService;
	
	@Autowired
	private WatcherManagerService watcherManagerService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private MetadataFieldGroupRepo metadataFieldGroupRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectLabelProfileRepo;
	
	@Value("${app.host}") 
   	private String host;
	
	@Value("${app.mount}") 
   	private String mount;
	
	@Value("${app.symlink.create}") 
   	private String link;
	
	private static final Logger logger = Logger.getLogger(SyncService.class);
		
	/**
	 * Default constructor.
	 * 
	 */
	public SyncService(){
		super();
	}
	
	/**
	 * SyncService runnable.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		if (logger.isDebugEnabled()) {
			logger.debug("Running Sync Service");
		}
				
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/metadata.json")));
		} catch (Exception e) {
			logger.error("Failed to read the metadata json",e);
		}
		
		Map<String, Object> projectMap = null;
		
		try {
			if(objectMapper == null && logger.isDebugEnabled()) {
				logger.debug("NULL OBJECT MAPPER");
			}
			projectMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			logger.error("Error with the Object Mapper",e);
		}
			
		String directory = null;
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/projects/";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Path> projects = fileList(directory);
        
        for(Path projectPath : projects) {
        	
        	String projectName = projectPath.getFileName().toString();
        	
        	Project project = projectRepo.create(projectName);
        	
        	List<Path> documents = fileList(projectPath.toString());
        	
        	List<Object> profileObjList = (List<Object>) projectMap.get(projectName);
        	
        	List<MetadataFieldGroup> fields = new ArrayList<MetadataFieldGroup>();
        	        	
        	if(!watcherManagerService.isWatcherServiceActive(projectName)) {
        		
        		logger.info("Watching: " + projectName);
        		
        		WatcherService watcherService = new WatcherService(projectName);
        		
        		AutowireCapableBeanFactory factory = appContext.getAutowireCapableBeanFactory();

        		factory.autowireBean( watcherService );
        		factory.initializeBean( watcherService, "bean" );
        		
	        	executorService.submit(watcherService);
	        	
	        	watcherManagerService.addActiveWatcherService(projectName);
        	}        	
        	
        	if(profileObjList == null) profileObjList = (List<Object>) projectMap.get("default");
        	
        	
        	for(Object metadata : profileObjList) {
        		
        		Map<String, Object> mMap = (Map<String, Object>) metadata;
				
				ProjectLabelProfile profile = projectLabelProfileRepo.create(project,
																			 mMap.get("gloss") == null ? "" : (String) mMap.get("gloss"), 
																			 mMap.get("repeatable") == null ? false : (Boolean) mMap.get("repeatable"), 
																			 mMap.get("readOnly") == null ? false : (Boolean) mMap.get("readOnly"),
																			 mMap.get("hidden") == null ? false : (Boolean) mMap.get("hidden"),
																			 mMap.get("required") == null ? false : (Boolean) mMap.get("required"),
																			 InputType.valueOf((String) mMap.get("inputType")),
																			 (String) mMap.get("default"));
				
				MetadataFieldLabel label = metadataFieldLabelRepo.create((String) mMap.get("label"), profile);
				
				fields.add(new MetadataFieldGroup(label));

				project.addProfile(profile);
				projectRepo.save(project);
        	}
        	
        	documents.parallelStream().forEach(documentPath -> {        		
        		
        		String documentName = documentPath.getFileName().toString();
        		
    			String pdfPath = mount + "/projects/"+projectName+"/"+documentName+"/"+documentName+".pdf";
				String txtPath = mount + "/projects/"+projectName+"/"+documentName+"/"+documentName+".txt";
        		String pdfUri = host+pdfPath;
        		String txtUri = host+txtPath;
        		
        		if(documentRepo.findByName(documentName) == null) {

        			logger.info("Adding: " + documentName);

	        		Document document = documentRepo.create(project, documentName, txtUri, pdfUri, txtPath, pdfPath, "Open");
	        		
	        		fields.stream().forEach(field -> {
						document.addField(metadataFieldGroupRepo.create(document, field.getLabel()));
					});
	        		
	        		
	        		FlatMARC flatMarc = null;
					try {
						flatMarc = new FlatMARC(voyagerService.getMARC(document.getName()));
					} catch (Exception e1) {
						logger.error("ERROR WHILE TRYING TO RETRIEVE MARC RECORD!!!",e1);
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
								logger.error("Illegal Argument",e);
							} catch (IllegalAccessException e) {
								logger.error("Illegal Access",e);
							}
			            }
			            else {
			            	try {
								marcList.add(field.get(flatMarc).toString());
							} catch (IllegalArgumentException e) {
								logger.error("Illegal Argument",e);
							} catch (IllegalAccessException e) {
								logger.error("Illegal Access",e);
							}
			            }
			            
			            metadataMap.put(field.getName().replace('_','.'), marcList);
			        }
					
					document.getFields().parallelStream().forEach(field -> {
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
						logger.error("CRASHED WHILE TRYING TO SEND DOCUMENT!!!",e);
		        		System.exit(-1);
		        	}
        		}
        	});
        	
        	try {
        		projectRepo.save(project);
        	}
        	catch(Exception e) {
				logger.error("CRASHED WHILE TRYING TO SAVE PROJECT!!!",e);
        		System.exit(-1);
        	}
        }
        logger.info("Sync Service Finished");
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
