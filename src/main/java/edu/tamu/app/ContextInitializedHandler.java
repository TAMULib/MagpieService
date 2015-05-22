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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
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

    /**
     * Method for event context refreshes.
     * 
     * @param		event		ContextRefreshedEvent
     * 
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {
    	taskExecutor.initialize();
    	taskExecutor.execute(new SyncService());
    	taskExecutor.execute(new WatcherService("projects"));
    	
    	taskScheduler.initialize();
    }  
    
}