package edu.tamu.app.observer;

import java.io.File;
import java.io.IOException;

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

    public ProjectFileListener(String root, String folder) {
        this.root = root;
        this.folder = folder;
        this.tika = new Tika();
    }

    private void createProject(File directory) {
        logger.info("ProjectFileListener is creating project " + directory.getName());
        projectService.getOrCreateProject(directory);
    }

    // this is a blocking sleep operation of this listener
    private boolean directoryIsReady(File directory) {
        if (isHeadless(directory)) {
        	long lastModified = 0L;
            long oldLastModified = -1L;
            long stableTime = 0L;
            // if a document directory in a headless project hasn't been modified in the last 10 seconds, it's probably ready
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
        logger.info("Creating document " + directory.getName());
        
        if (directoryIsReady(directory)) {
        	projectService.createDocument(directory);
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
    	String documentName = file.getParentFile().getName();
    	String projectName = file.getParentFile().getParentFile().getName();
    	logger.info("Adding file " + file.getName() + " to " + documentName + " in project " + projectName);
    	Document document = documentRepo.findByProjectNameAndName(projectName, documentName);
    	addResource(document, file);
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
    	return projectService.projectIsHeadless(directory.getParentFile().getName());
    }
    
    private String projectDocumentKey(String projectName, String documentName) {
    	return String.join("-", projectName, documentName);
    }
    
    private void addResource(Document document, File file) {
    	String name = file.getName();
		String path = document.getDocumentPath() + File.separator + file.getName();
		String url = host + document.getDocumentPath() + File.separator + file.getName();
		String mimeType = tika.detect(path);
		Resource resource = new Resource(name, path, url, mimeType);
		document.addResource(resource);
		documentRepo.save(document);
    }

}
