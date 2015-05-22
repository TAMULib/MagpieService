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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.WatcherService;

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
    	
    	ExecutorService executor = executorService();
    	executor.execute(new SyncService());
    	executor.execute(new WatcherService("projects"));
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
    	return application.sources(WebServerInit.class);
    }
    
    /**
     * Thread pool task executor configuration.
     * 
     * @return		ThreadPoolTaskExecutor
     * 
     */
    @Bean(name="executor")
    private static ExecutorService executorService() {
    	ExecutorService executor = Executors.newFixedThreadPool(10);
    	
    	return executor;
    }
    
}
