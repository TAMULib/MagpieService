/* 
 * ControllerConfig.java 
 * 
 * Version: 
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
package edu.tamu.app.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/** 
 * Web MVC Configuration for application controller.
 * 
 * @author
 *
 */
@Configuration
@ComponentScan(basePackages = {"edu.tamu.app.config", "edu.tamu.app.controller"})
@ConfigurationProperties(prefix="app.controller")
public class MetadataToolWebAppConfig extends WebMvcConfigurerAdapter {
			
	 /**
     * Executor Service configuration.
     * 
     * @return		ExecutorSevice
     * 
     */
    @Bean(name="executorService")
    private static ExecutorService configureExecutorService() {
    	ExecutorService executorService = new ThreadPoolExecutor(10, 25, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(25));
       	return executorService;
	}
	
}
