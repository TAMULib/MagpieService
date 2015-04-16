package edu.tamu.app;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
public class ApplicationContextProvider implements ApplicationContextAware {
	
	public static ApplicationContext appContext;
	
	public ApplicationContextProvider() {}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		
		appContext = ac;
		
	}
	
}