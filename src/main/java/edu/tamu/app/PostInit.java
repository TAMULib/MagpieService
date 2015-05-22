package edu.tamu.app;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.WatcherService;

@Component
public class PostInit {
	
	@Autowired 
	private ThreadPoolTaskExecutor taskExecutor;
	
	public PostInit() {}
   
    @PostConstruct
    public void setup(){
        taskExecutor.initialize();
        taskExecutor.execute(new SyncService());
    	taskExecutor.execute(new WatcherService("projects"));
    }
}