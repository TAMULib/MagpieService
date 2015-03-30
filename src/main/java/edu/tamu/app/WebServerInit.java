/* 
 * WebServerInit.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import edu.tamu.app.service.SyncService;

/** 
 * Web server initialization.
 * 
 * @author
 *
 */
@ComponentScan
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
public class WebServerInit extends SpringBootServletInitializer {
		
	/**
	 * Entry point to the application from within servlet.
	 *
	 * @param       args    		String[]
	 *
	 */
    public static void main(String[] args) {
        SpringApplication.run(WebServerInit.class, args);        
        ThreadPoolTaskExecutor taskExecutor = configureTaskExecutor();  
        taskExecutor.initialize();
        taskExecutor.execute(new SyncService());
    }
    
    /**
   	 * Entry point to the application if run using spring-boot:run.
   	 *
   	 * @param       application    	SpringApplicationBuilder
   	 *
   	 * @return		SpringApplicationBuilder
   	 *
   	 */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    	SpringApplicationBuilder builder = application.sources(WebServerInit.class);    	 
    	ThreadPoolTaskExecutor taskExecutor = configureTaskExecutor();
    	taskExecutor.initialize();
        taskExecutor.execute(new SyncService());
        return builder;
    }
    
    @Bean
    private static ThreadPoolTaskExecutor configureTaskExecutor() {
    	ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    	taskExecutor.setCorePoolSize(5);
    	taskExecutor.setMaxPoolSize(10);
    	taskExecutor.setQueueCapacity(25);
    	return taskExecutor;
    }
   
}
