package edu.tamu.app.observer;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.ProjectsService;
import edu.tamu.app.utilities.FileSystemUtility;

@Component
@Scope("prototype")
public class ProjectFileListener extends AbstractFileListener {

    @Value("${app.document.create.wait}")
    private int documentCreationWait;

    @Value("${app.host}")
    private String host;

    @Value("${app.mount}")
    private String mount;

    private static final Logger logger = Logger.getLogger(ProjectFileListener.class);

    @Autowired
    private ProjectsService projectService;

    public ProjectFileListener(String root, String folder) {
        this.root = root;
        this.folder = folder;
    }

    private void createProject(File directory) {
        logger.info("ProjectFileListener is creating project " + directory.getName());
        projectService.getOrCreateProject(directory);
    }

    private boolean directoryIsReady(File directory) {
        if (projectService.projectIsHeadless(projectService.getName(directory.getParentFile())) == false) {
            return true;
        } else {
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
        }
    }

    private void createDocument(File directory) {
        logger.info("ProjectFileListener is creating document " + directory.getName());

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

}
