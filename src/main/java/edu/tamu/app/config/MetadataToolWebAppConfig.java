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

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import edu.tamu.app.controller.interceptor.AppRestInterceptor;

/**
 * Web MVC Configuration for application controller.
 * 
 * @author
 *
 */
@Configuration
@ConfigurationProperties(prefix = "app.controller")
@EntityScan(basePackages = { "edu.tamu.app.model" })
@EnableJpaRepositories(basePackages = { "edu.tamu.app.model.repo" })
@ComponentScan(basePackages = { "edu.tamu.app.config", "edu.tamu.app.controller" })
public class MetadataToolWebAppConfig extends WebMvcConfigurerAdapter {

    /**
     * Executor Service configuration.
     * 
     * @return ExecutorSevice
     * 
     */
    @Bean(name = "executorService")
    private static ExecutorService configureExecutorService() {
        ExecutorService executorService = new ThreadPoolExecutor(10, 25, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(25));
        return executorService;
    }

    /**
     * Rest interceptor bean.
     *
     * @return RestInterceptor
     *
     */
    @Bean
    public AppRestInterceptor restInterceptor() {
        return new AppRestInterceptor();
    }

    /**
     * Add interceptor to interceptor registry.
     *
     * @param registry
     *            InterceptorRegistry
     *
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restInterceptor()).addPathPatterns("/rest/**");
    }

}
