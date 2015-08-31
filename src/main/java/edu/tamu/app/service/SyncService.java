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

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;

import edu.tamu.app.ApplicationContextProvider;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.impl.MetadataLabelImpl;
import edu.tamu.app.model.repo.DocumentRepo;

/** 
 * Sync Service. Synchronizes project database with projects folders.
 * 
 * @author
 *
 */
@Component
@Service
@PropertySource("classpath:/config/application.properties")
public class SyncService implements Runnable {
		
	/**
	 * Default constructor.
	 * 
	 */
	public SyncService() {
		super();
	}

	/**
	 * SyncService runnable.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		System.out.println("Running Sync Service");
		
		DocumentRepo docRepo = (DocumentRepo) ApplicationContextProvider.appContext.getBean("documentRepo");
		Environment env = ApplicationContextProvider.appContext.getEnvironment();
		
		SimpMessagingTemplate simpMessagingTemplate = (SimpMessagingTemplate) ApplicationContextProvider.appContext.getBean("brokerMessagingTemplate");
		
		ExecutorService executorService = (ExecutorService) ApplicationContextProvider.appContext.getBean("executorService");
		
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/metadata.json")));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		ObjectMapper objectMapper = (ObjectMapper) ApplicationContextProvider.appContext.getBean("objectMapper");
		
		Map<String, Object> projectMap = null;
		
		try {
			projectMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		List<MetadataLabelImpl> metadataLabels;
		
		String host = env.getProperty("app.host");
		String mount = env.getProperty("app.mount");
		
		String directory = null;
		try {
			directory = ApplicationContextProvider.appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Path> projects = fileList(directory);
        
        for(Path project : projects) {
        	List<Path> documents = fileList(project.toString());
        	
        	System.out.println(project.getFileName().toString());
        	List<Object> profile = (List<Object>) projectMap.get(project.getFileName().toString());
        	
        	executorService.submit(new WatcherService(project.getFileName().toString()));
        	
        	for(Path document : documents) {
    			
    			if(profile == null) profile = (List<Object>) projectMap.get("default");
    			
    			metadataLabels = new ArrayList<MetadataLabelImpl>();
    			
    			for(Object metadata : profile) {
    				
    				Map<String, Object> mMap = (Map<String, Object>) metadata;
    				MetadataLabelImpl metadataProfile = new MetadataLabelImpl((String) mMap.get("label"), 
    																  (String) mMap.get("gloss"), 
    																  (boolean) mMap.get("repeatable"), 
    																  (boolean) mMap.get("readOnly"),
    																  (Boolean) mMap.get("required"), 
    																  InputType.valueOf((String) mMap.get("inputType")),(String) mMap.get("default"));
    				metadataLabels.add(metadataProfile);
    			}
    			    			
    			if((docRepo.findByName(document.getFileName().toString()) == null)) {
            		
            		String pdfUri = host+"/mnt/projects/"+project.getFileName().toString()+"/"+document.getFileName().toString()+"/"+document.getFileName().toString()+".pdf";
            		String txtUri = host+"/mnt/projects/"+project.getFileName().toString()+"/"+document.getFileName().toString()+"/"+document.getFileName().toString()+".txt";
	
					DocumentImpl doc = new DocumentImpl(document.getFileName().toString(), project.getFileName().toString(), txtUri, pdfUri, "Open", metadataLabels);
					docRepo.save(doc);
					
					Map<String, Object> docMap = new HashMap<String, Object>();
					docMap.put("document", doc);
					docMap.put("isNew", "true");
					simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse("success", docMap, new RequestId("0")));
					
				}
    			
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
