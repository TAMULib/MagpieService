package edu.tamu.app.observer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.service.ProjectsService;
import edu.tamu.app.utilities.FileSystemUtility;

@Component
@Scope("prototype")
public class ProjectFileListener extends AbstractFileListener {
	
	private static final Logger logger = Logger.getLogger(ProjectFileListener.class);

    @Value("${app.document.create.wait}")
    private int documentCreationWait;

    @Value("${app.host}")
    private String host;

    @Value("${app.mount}")
    private String mount;
    
    @Autowired
    private ProjectsService projectService;
    
    @Autowired
    private DocumentRepo documentRepo;
    
    private Tika tika;
    
    private Optional<Document> document;
    
    private List<Resource> resources;

    public ProjectFileListener(String root, String folder) {
        this.root = root;
        this.folder = folder;
        this.tika = new Tika();
        this.document = Optional.empty();
        this.resources = new ArrayList<Resource>();
    }

    private void createProject(File directory) {
        logger.info("ProjectFileListener is creating project " + directory.getName());
        projectService.getOrCreateProject(directory);
    }

    private boolean directoryIsReady(File directory) {
        if (isHeadless(directory)) {
        	long lastModified = 0L;
            long oldLastModified = -1L;
            long stableTime = 0L;
            // if a document directory in a headless project hasn't been modified in the last 10
            // seconds, it's probably ready
            while ((oldLastModified < lastModified) || (oldLastModified == lastModified) && (System.currentTimeMillis() - stableTime) < documentCreationWait) {
                lastModified = directory.lastModified();
                if ((lastModified != oldLastModified) || stableTime == 0L) {
                    stableTime = System.currentTimeMillis();
                }
                oldLastModified = lastModified;
            }
            return true;
        } else {
            return true;
        }
    }

    private void createDocument(File directory) {
        logger.info("ProjectFileListener is creating document " + directory.getName());

        resources.clear();
        
        if (directoryIsReady(directory)) {
        	document = projectService.createDocument(directory);
        	
        	if (isHeadless(directory)) {
        		document.get().setResources(resources);
        		documentRepo.save(document.get());
        		document = Optional.empty();
        	}
        }
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {
        if (FileSystemUtility.getWindowsSafePath(directory.getParent()).equals(FileSystemUtility.getWindowsSafePath(getPath()))) {
            createProject(directory);
        } else {
            createDocument(directory);
        }
    }

    @Override
    public void onDirectoryChange(File directory) {

    }

    @Override
    public void onDirectoryDelete(File directory) {

    }

    @Override
    public void onFileCreate(File file) {
    	
    	if(document.isPresent()) {
    		String name = file.getName();
    		String path = document.get().getDocumentPath() + File.separator + file.getName();
    		String url = host + document.get().getDocumentPath() + File.separator + file.getName();
    		String mimeType = tika.detect(path);

    		Resource resource = new Resource(name, path, url, mimeType);

    		if (isHeadless(file.getParentFile())) {
    			resources.add(resource);
        	}
        	else {
        		document.get().addResource(resource);
        		document = Optional.of(documentRepo.save(document.get()));
        	}
    	}    	
    	
    }

    @Override
    public void onFileChange(File file) {

    }

    @Override
    public void onFileDelete(File file) {

    }

    @Override
    public void onStop(FileAlterationObserver observer) {

    }
    
    private boolean isHeadless(File directory) {
    	return projectService.projectIsHeadless(projectService.getName(directory.getParentFile()));
    }

}
