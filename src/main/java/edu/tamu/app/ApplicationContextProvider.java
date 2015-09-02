/* 
 * ApplicationContextProvider.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;

/**
 * Sets an application context for use during initialization of the app.
 * Used to start watcher service during app startup.
 * 
 * @author 
 *
 */
@ComponentScan(basePackages={"edu.tamu.framework", "edu.tamu.app"})
@SpringBootApplication
public class ApplicationContextProvider implements ApplicationContextAware {
	
	public static ApplicationContext appContext;
	
	public ApplicationContextProvider() {}

	/**
	 * Sets the application context.
	 * 
	 * @param		ac				ApplicationContext
	 * 
	 * @exception   BeansException
	 * 
	 */
	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		appContext = ac;
	}
	
}
