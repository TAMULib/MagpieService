/* 
 * ContextClosedHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * Handler for when the servlet context closes.
 * 
 * @author
 *
 */
@Component
public class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private ExecutorService executorService;

    /**
     * Method for event context close.
     * 
     * @param event
     *            ContextClosedEvent
     * 
     */
    public void onApplicationEvent(ContextClosedEvent event) {
        executorService.shutdownNow();
    }

}
