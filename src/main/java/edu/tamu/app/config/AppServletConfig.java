/* 
 * ServletConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.config;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A tool to be able to customize servlets.
 * 
 * @author Micah Cooper
 *
 */
@Configuration
public class AppServletConfig {
	
	/**
	 * @return      ServletCustomizer
	 */
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return new ServletCustomizer();
	}
	
	private static class ServletCustomizer implements EmbeddedServletContainerCustomizer {	

		@Override
		public void customize(ConfigurableEmbeddedServletContainer cesc) {
			// An example on how to customize a servlet.
			//MimeMappings mm = new MimeMappings(MimeMappings.DEFAULT);
			//mm.add("png", "image/png");
			//cesc.setMimeMappings(mm);
		}		
	}
}

