package edu.tamu.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.WatcherService;

@Component
public class ContextSetup implements ApplicationListener<ContextStartedEvent> {

	@Autowired 
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Override
	public void onApplicationEvent(ContextStartedEvent event) {
		taskExecutor.initialize();
        taskExecutor.execute(new SyncService());
    	taskExecutor.execute(new WatcherService("projects"));		
	}

}
