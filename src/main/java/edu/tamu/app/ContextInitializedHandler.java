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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.MapWatcherManagerService;
import edu.tamu.app.service.MapWatcherService;
import edu.tamu.app.service.SyncService;
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
	private ApplicationContext appContext;
	
	@Autowired
	private ExecutorService executorService;
	
	@Autowired
	private WatcherManagerService watcherManagerService;

	@Autowired
	private MapWatcherManagerService mapWatcherManagerService;

	@Autowired
	private SyncService syncService;
	
	@Autowired
	private WatcherService watcherService;

	@Autowired
	private MapWatcherService mapWatcherService;

	@Value("${app.mount}") 
   	private String mount;
			
    @Value("${app.symlink.create}") 
	private String createSymlink;
    
    /**
     * Method for event context refreshes.
     * 
     * @param		event		ContextRefreshedEvent
     * 
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	
    	if(appContext == null) {
			System.out.println("APP CONTEXT IS NULL");
		}
    	
    	if(createSymlink.equals("true")) {
    		
			try {
				FileUtils.deleteDirectory( new File(event.getApplicationContext().getResource("classpath:static").getFile().getAbsolutePath() + mount) );
			} catch (IOException e) {
				System.out.println("\nDIRECTORY DOES NOT EXIST\n");
			}
    		
    		try {    			
				Files.createSymbolicLink( Paths.get(event.getApplicationContext().getResource("classpath:static").getFile().getAbsolutePath() + mount), Paths.get("/mnt" + mount));
			} catch (FileAlreadyExistsException e) {
				System.out.println("\nSYMLINK ALREADY EXISTS\n");
			} catch (IOException e) {
				System.out.println("\nFAILED TO CREATE SYMLINK!!!\n");
				e.printStackTrace();
			}
    	}
    	
    	executorService.submit(syncService);
    	
    	System.out.println("Watching: projects");
    	
    	watcherService.setFolder("projects");
    	
    	executorService.submit(watcherService);
    	
    	watcherManagerService.addActiveWatcherService("projects");

    	System.out.println("Watching: maps");
    	
    	mapWatcherService.setFolder("maps");
    	
    	executorService.submit(mapWatcherService);
    	
    	mapWatcherManagerService.addActiveWatcherService("maps");

    }  
    
}
