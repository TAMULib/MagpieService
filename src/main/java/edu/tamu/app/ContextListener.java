package edu.tamu.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public class ContextListener implements ServletContextListener {

	@Autowired 
	private ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired 
	private ThreadPoolTaskScheduler taskScheduler;
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		taskExecutor.destroy();
		taskScheduler.destroy();
	}

}
