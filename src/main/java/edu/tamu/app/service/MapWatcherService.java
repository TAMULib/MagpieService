/* 
 * WatcherService.java 
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

import java.lang.reflect.Field;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.response.marc.FlatMARC;

/** 
 * Watches map file folder, harvests contents, and updates app data as needed
 * 
 * @author
 *
 */
@Service
@Scope(value = "prototype")
public class MapWatcherService implements Runnable {
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ExecutorService executorService;
	
	@Autowired
	private WatcherManagerService watcherManagerService;
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Value("${app.host}") 
   	private String host;
	
	@Value("${app.mount}") 
   	private String mount;
	
	@Value("${app.symlink.create}") 
   	private String link;
	
	private String folder;
	
	/**
	 * Default constructor.
	 * 
	 */
	public MapWatcherService(){
		super();
	}
	
	public MapWatcherService(String folder) {
		super();
		this.folder = folder;
	}
	
	public String getFolder() {
		return folder;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	/**
	 * WatcherService runnable.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		//check for map files, and do stuff with them
		System.out.println("Im running so fast");
		String directory = "";
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/" + folder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = FileSystems.getDefault().getPath(directory, "");
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
             
            System.out.println("MapWatch Service registered for dir: " + dir.getFileName());
             
            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path file = ev.context();

                    
                    System.out.println(kind.name() + ": " + file);
                    if (kind == ENTRY_CREATE) {
                    	String line;
                    	try {
                    		InputStream stream = new FileInputStream(directory+"/"+file.toFile());
                    	    InputStreamReader sReader = new InputStreamReader(stream);
                    		BufferedReader bReader = new BufferedReader(sReader);
                    		while ((line = bReader.readLine()) != null) {
                    			System.out.println("Stringer: "+line);
                    		}
                    	} catch (IOException e) {
                            System.err.println(e);
                    	}
                    	
                    	//read and iterate over imports
                    	//get project based on bibid = project name?
                    	//update all project documents with PUBLISHED status
                    	//unlock project (if all documents have been PUBLISHED
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
	}
}
