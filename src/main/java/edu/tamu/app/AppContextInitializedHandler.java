/* 
 * ContextInitializedHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.tamu.framework.CoreContextInitializedHandler;

/**
 * Handler for when the servlet context refreshes.
 * 
 * @author
 *
 */
@Component
@Profile(value = { "!test" })
public class AppContextInitializedHandler extends CoreContextInitializedHandler {

    /**
     * Method for event context refreshes.
     * 
     * @param event
     *            ContextRefreshedEvent
     * 
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }

    @Override
    protected void before(ContextRefreshedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void after(ContextRefreshedEvent event) {
        // TODO Auto-generated method stub

    }

}
