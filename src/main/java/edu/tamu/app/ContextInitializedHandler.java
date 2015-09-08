/* 
 * ContextInitializedHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.WatcherService;

/** 
 * Handler for when the servlet context refreshes.
 * 
 * @author
 *
 */
@Component
@ConditionalOnWebApplication
public class ContextInitializedHandler implements ApplicationListener<ContextRefreshedEvent> {
    
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectLabelProfileRepo;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;

	@Autowired
	private Environment env;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private ExecutorService executorService;
	
	@Autowired
	private ObjectMapper objectMapper;
		
    @Value("${app.create.symlink}") 
	private String createSymlink;
    
    @Value("${app.mount}") 
   	private String mount;
    
    /**
     * Method for event context refreshes.
     * 
     * @param		event		ContextRefreshedEvent
     * 
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	
    	if(createSymlink.equals("true")) {
    		try {
				Files.createSymbolicLink( Paths.get(event.getApplicationContext().getResource("classpath:static/mnt").getFile().getAbsolutePath() + "/projects"), Paths.get(mount));
			} catch (IOException e) {
				System.out.println("\nFAILED TO CREATE SYMLINK!!!\n");				
				e.printStackTrace();
			}
    	}

    	executorService.submit(new SyncService(projectRepo,
    										   documentRepo,
    										   projectLabelProfileRepo,
    										   metadataFieldRepo,
    										   metadataFieldLabelRepo,
			      							   env,
			      							   appContext,
			      							   simpMessagingTemplate,
			      							   executorService,
			      							   objectMapper));
    	
    	executorService.submit(new WatcherService(projectRepo,
				   								  documentRepo,
				   								  projectLabelProfileRepo,
    											  metadataFieldRepo,
    											  metadataFieldLabelRepo,
					  						      env,
					  						      appContext,
					  						      simpMessagingTemplate,
					  						      executorService,
					  						      objectMapper,
					  							  "projects"));

    }  
    
}
