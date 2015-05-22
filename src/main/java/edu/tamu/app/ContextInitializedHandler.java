package edu.tamu.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.WatcherService;

@Component
class ContextInitializedHandler implements ApplicationListener<ContextRefreshedEvent> {
    
	@Autowired 
    private ThreadPoolTaskExecutor taskExecutor;
	
    @Autowired 
    private ThreadPoolTaskScheduler taskScheduler;

    public void onApplicationEvent(ContextRefreshedEvent event) {
    	taskExecutor.initialize();
    	taskExecutor.execute(new SyncService());
    	taskExecutor.execute(new WatcherService("projects"));
    	
    	taskScheduler.initialize();
    }  
    
}