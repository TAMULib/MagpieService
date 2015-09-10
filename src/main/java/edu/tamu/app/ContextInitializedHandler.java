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
import java.nio.file.FileAlreadyExistsException;
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
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.VoyagerService;
import edu.tamu.app.service.WatcherManagerService;
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
	private VoyagerService voyagerService; 
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectLabelProfileRepo;
	
	@Autowired
	private MetadataFieldGroupRepo metadataFieldRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;

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
	
	@Autowired
	private WatcherManagerService watcherManagerService;
	
	@Value("${app.mount}") 
   	private String mount;
	
	@Value("${app.symlink}") 
	private String symlink;
		
    @Value("${app.symlink.create}") 
	private String createSymlink;
    
    /**
     * Method for event context refreshes.
     * 
     * @param		event		ContextRefreshedEvent
     * 
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	
    	if(createSymlink.equals("true")) {
    		try {
				Files.createSymbolicLink( Paths.get(event.getApplicationContext().getResource("classpath:static/mnt").getFile().getAbsolutePath() + symlink), Paths.get(mount));
			} catch (FileAlreadyExistsException e) {
				System.out.println("\nSYMLINK ALREADY EXISTS\n");
			} catch (IOException e) {
				System.out.println("\nFAILED TO CREATE SYMLINK!!!\n");
				e.printStackTrace();
			}
    	}
    	
    	executorService.submit(new SyncService(watcherManagerService,
    										   voyagerService,
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
			      							   objectMapper));
    	
    	System.out.println("Watching: projects");
    	
    	executorService.submit(new WatcherService(watcherManagerService,
    											  voyagerService,
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
					  							  "projects"));
    	
    	watcherManagerService.addActiveWatcherService("projects");

    }  
    
}
