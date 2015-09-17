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

import java.lang.reflect.Field;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.response.marc.FlatMARC;

/** 
 * Watcher Service. Watches projects folder and inserts created documents into database.
 * 
 * @author
 *
 */
@Service
@Scope(value = "prototype")
public class WatcherService implements Runnable {
	
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
	
	private String folder;
	
	/**
	 * Default constructor.
	 * 
	 */
	public WatcherService(){
		super();
	}
	
	public WatcherService(String folder) {
		super();
		this.folder = folder;
	}
	
	public String getFolder() {
		return folder;
	}
	
	public void setFolder(String folder) {
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
		
		String directory = "";
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/" + folder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Project project = projectRepo.findByName(folder);
		
		
		if(!folder.equals("projects")) {
			
			try {
				directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/projects/" + folder;
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
				
				ProjectLabelProfile profile = projectLabelProfileRepo.create(projectRepo.findByName(folder),
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
                    		
                    		if(!watcherManagerService.isWatcherServiceActive(docString)) {
                    			
                    			WatcherService watcherService = new WatcherService(docString);
                        		
                        		AutowireCapableBeanFactory factory = appContext.getAutowireCapableBeanFactory();

                        		factory.autowireBean( watcherService );
                        		factory.initializeBean( watcherService, "bean" );
                        		
                    			executorService.submit(watcherService);
                    			
                    			watcherManagerService.addActiveWatcherService(docString);
                    		}
                    	}
                    	else {
                    		
	                    	if((documentRepo.findByName(docString) == null)) {
	                    		
	        					String pdfPath = mount + "/projects/"+folder+"/"+docString+"/"+docString+".pdf";
	            				String txtPath = mount + "/projects/"+folder+"/"+docString+"/"+docString+".txt";
	            				
	                    		String pdfUri = host+pdfPath;
	                    		String txtUri = host+txtPath;
	                         		
	        					Document document = documentRepo.create(project, docString, txtUri, pdfUri, txtPath, pdfPath, "Open");
	        					
	        					fields.parallelStream().forEach(field -> {
	        						document.addField(metadataFieldGroupRepo.create(document, field.getLabel()));
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
