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

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import edu.tamu.app.ApplicationContextProvider;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.impl.MetadataLabelImpl;
import edu.tamu.app.model.repo.DocumentRepo;

/** 
 * Sync Service.
 * 
 * @author
 *
 */
@Component
@Service
@PropertySource("classpath:/config/application.properties")
public class WatcherService implements Runnable {
	private String folder;
	
	public WatcherService(){}
	
	public WatcherService(String folder) {
		this.folder = folder;
	}
		
	/**
	 * SyncService runnable.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		DocumentRepo docRepo = (DocumentRepo) ApplicationContextProvider.appContext.getBean("documentRepo");
		Environment env = ApplicationContextProvider.appContext.getEnvironment();
		
		SimpMessagingTemplate simpMessagingTemplate = (SimpMessagingTemplate) ApplicationContextProvider.appContext.getBean("brokerMessagingTemplate");
		
		ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) ApplicationContextProvider.appContext.getBean("taskExecutor");
		
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/metadata.json")));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		ObjectMapper om = (ObjectMapper) ApplicationContextProvider.appContext.getBean("objectMapper");
		
		Map<String, Object> projectMap = null;
		
		try {
			projectMap = om.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		List<MetadataLabelImpl> metadataLabels = new ArrayList<MetadataLabelImpl>();
		
		String directory = env.getProperty("app.directory") + "/" + folder;
		
		if(!folder.equals("projects")) {
			
			directory = env.getProperty("app.directory") + "/projects/" + folder;
			List<Object> profile = (List<Object>) projectMap.get(folder);
			
			if(profile == null) profile = (List<Object>) projectMap.get("default");
			
			for(Object metadata : profile) {
				
				Map<String, Object> mMap = (Map<String, Object>) metadata;
				
				MetadataLabelImpl metadataProfile = new MetadataLabelImpl((String) mMap.get("label"), 
																  (String) mMap.get("gloss"), 
																  (boolean) mMap.get("repeatable"), 
																  InputType.valueOf((String) mMap.get("inputType")));
				metadataLabels.add(metadataProfile);
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
                    		taskExecutor.execute(new WatcherService(docString));
                    	}
                    	else {
                    	
	                    	if((docRepo.findByName(docString) == null)) {
	                    		
	                    		String pdfUri = "http://localhost:9000/mnt/projects/"+folder+"/"+docString+"/"+docString+".pdf";
	                    		String txtUri = "http://localhost:9000/mnt/projects/"+folder+"/"+docString+"/"+docString+".txt";
	                         		
	        					DocumentImpl doc = new DocumentImpl(docString, txtUri, pdfUri, "Open", metadataLabels);
	        					docRepo.save(doc);
	        					
	        					Map<String, Object> docMap = new HashMap<String, Object>();
	        					docMap.put("document", doc);
	        					docMap.put("isNew", "true");
	        					simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResImpl("success", docMap, new RequestId("0")));
	        					
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
