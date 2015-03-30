/* 
 * SyncService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.repo.DocumentRepo;

/** 
 * Sync Service.
 * 
 * @author
 *
 */
@Component
@Service
@PropertySource("classpath:/config/application.properties")
public class SyncService implements Runnable, ApplicationContextAware {
	
	private static ApplicationContext ac;
		
	@Override
	public void run() {
		
		DocumentRepo docRepo = (DocumentRepo) ac.getBean("documentRepo");
		Environment env = ac.getEnvironment();
		
		String directory = env.getProperty("app.directory");
		
		try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = FileSystems.getDefault().getPath(directory, "");
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
             
            System.out.println("Watch Service registered for dir: " + dir.getFileName());
             
            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                     
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                     
                    System.out.println(kind.name() + ": " + fileName);
                                         
                    if (kind == ENTRY_CREATE) {
                    	if(docRepo.getDocumentByFilename(fileName.toString()) == null) {					
        					DocumentImpl doc = new DocumentImpl(fileName.toString(), "Unassigned");
        					docRepo.save(doc);
        				}
                    }                    
                    else if(kind == ENTRY_MODIFY) {
                    	
                    }
                    else if(kind == ENTRY_DELETE) {
                    	
                    }
                    else {
                    	
                    }
                    
                }
                 
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
             
        } catch (IOException ex) {
            System.err.println(ex);
        }
		
	}

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		SyncService.ac = ac;
	}
	
}
