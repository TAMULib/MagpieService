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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.WatcherService;

/** 
 * Handler for when the servlet context refreshes.
 * 
 * @author
 *
 */
@Component
class ContextInitializedHandler implements ApplicationListener<ContextRefreshedEvent> {
    
	@Autowired 
    private ExecutorService executorService;
	
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
    	
    	executorService.submit(new SyncService());
    	executorService.submit(new WatcherService("projects"));
    	
    }  
    
}