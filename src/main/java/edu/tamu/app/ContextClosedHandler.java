/* 
 * ContextClosedHandler.java 
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
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/** 
 * Handler for when the servlet context closes.
 * 
 * @author
 *
 */
@Component
class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {
    
	@Autowired 
    private ThreadPoolTaskExecutor taskExecutor;
	
    @Autowired 
    private ThreadPoolTaskScheduler taskScheduler;

    /**
     * Method for event context close.
     * 
     * @param		event		ContextClosedEvent
     * 
     */
    public void onApplicationEvent(ContextClosedEvent event) {
    	taskExecutor.shutdown();
    	taskExecutor.shutdown();
    }  
    
}