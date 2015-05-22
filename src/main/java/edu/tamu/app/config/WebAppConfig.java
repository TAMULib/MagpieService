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

import java.util.List;

import javax.xml.transform.Source;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.tamu.app.ApplicationContextProvider;
import edu.tamu.app.controller.interceptor.RestInterceptor;
import edu.tamu.app.service.HttpService;
import edu.tamu.app.service.VoyagerService;

/** 
 * Web MVC Configuration for application controller.
 * 
 * @author
 *
 */
@Configuration
@ComponentScan(basePackages = "edu.tamu.app.controller")
@ConfigurationProperties(prefix="app.controller")
public class WebAppConfig extends WebMvcConfigurerAdapter{	

	/**
	 * Configures message converters.
	 *
	 * @param       converters    	List<HttpMessageConverter<?>>
	 *
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	    StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
	    stringConverter.setWriteAcceptCharset(false);
	    converters.add(new ByteArrayHttpMessageConverter());
	    converters.add(stringConverter);
	    converters.add(new ResourceHttpMessageConverter());
	    converters.add(new SourceHttpMessageConverter<Source>());
	    converters.add(new AllEncompassingFormHttpMessageConverter());
	    converters.add(jackson2Converter());
	}

	/**
	 * Set object mapper to jackson converter bean.
	 *
	 * @return      MappingJackson2HttpMessageConverter
	 *
	 */
	@Bean
	public MappingJackson2HttpMessageConverter jackson2Converter() {
	    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
	    converter.setObjectMapper(objectMapper());
	    return converter;
	}
	
	/**
	 * Object mapper bean.
	 *
	 * @return     	ObjectMapper
	 *
	 */
	@Bean
	public ObjectMapper objectMapper() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);	    
	    return objectMapper;
	}
	
	/**
     * Thread pool task executor configuration.
     * 
     * @return		ThreadPoolTaskExecutor
     * 
     */
    @Bean(name="taskExecutor")
    private static ThreadPoolTaskExecutor configureTaskExecutor() {
    	ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    	taskExecutor.setCorePoolSize(5);
    	taskExecutor.setMaxPoolSize(10);
    	taskExecutor.setQueueCapacity(25);
    	return taskExecutor;
    }
    
    /**
     * Application context provider bean.
     * 
     * @return		ApplicationContextProvider
     * 
     */
    @Bean(name="appContextProvider")
    private static ApplicationContextProvider appContextProvider() {
    	return new ApplicationContextProvider();
    }
    
	/**
	 * Rest interceptor bean.
	 *
	 * @return      RestInterceptor
	 *
	 */
	@Bean
	public RestInterceptor jwtInterceptor() {
	    return new RestInterceptor();
	}
	
	/**
	 * Add interceptor to interceptor registry.
	 *
	 * @param       registry	   InterceptorRegistry
	 *
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(jwtInterceptor()).addPathPatterns("/rest/**");
	}
	
	/** 
	 * Http Service bean.
	 *
	 * @return      HttpService
	 *
	 */
	@Bean
	public HttpService httpService() {
	    return new HttpService();
	}
	
	/**
	 * Voyager Service bean.
	 *
	 * @return      VoyagerService
	 *
	 */
	@Bean
	public VoyagerService voyagerService() {
	    return new VoyagerService();
	}
		
}
