/* 
 * SyncScheduleConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import edu.tamu.app.service.SyncService;

/** 
 * Time Schedule Configuration.
 * 
 * @author
 *
 */
@Configuration
@EnableScheduling
public class SyncScheduleConfig implements SchedulingConfigurer
{
	@Bean	
	public SyncService timeService() {
		return new SyncService();
	}

	@Bean()
	public ThreadPoolTaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
	{
		taskRegistrar.setTaskScheduler(taskScheduler());
	}
	
}
