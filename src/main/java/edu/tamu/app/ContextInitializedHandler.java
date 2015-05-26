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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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
    private ThreadPoolTaskExecutor taskExecutor;
	
    @Autowired 
    private ThreadPoolTaskScheduler taskScheduler;
    
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
    	
    	System.out.println("\n\n" + mount + "\n\n");
    	
    	if(createSymlink.equals("true")) {
    		System.out.println("\n CREATING SYMLINK TO MOUNT \n");
    		try {
    			System.out.println("\n" + Paths.get(ApplicationContextProvider.appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath()) + "\n");
    			System.out.println("\n" + Paths.get(mount) + "\n");
				Files.createSymbolicLink(Paths.get(ApplicationContextProvider.appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath()), Paths.get(mount));
			} catch (IOException e) {
				System.out.println("\n FAILED TO CREATE SYMLINK \n");				
				e.printStackTrace();
			}
    	}
    	
    	taskExecutor.initialize();
    	taskExecutor.execute(new SyncService());
    	taskExecutor.execute(new WatcherService("projects"));
    	
    	taskScheduler.initialize();
    }  
    
}