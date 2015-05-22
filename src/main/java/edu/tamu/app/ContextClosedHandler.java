package edu.tamu.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {
    
	@Autowired 
    private ThreadPoolTaskExecutor executor;
	
    @Autowired 
    private ThreadPoolTaskScheduler scheduler;

    public void onApplicationEvent(ContextClosedEvent event) {
        scheduler.shutdown();
        executor.shutdown();
    }  
    
}