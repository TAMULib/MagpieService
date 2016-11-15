package edu.tamu.app.observer;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.ProjectsService;
import edu.tamu.app.utilities.FileSystemUtility;

@Component
@Scope("prototype")
public class ProjectFileListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(ProjectFileListener.class);

    @Autowired
    private ProjectsService projectService;

    public ProjectFileListener(String root, String folder) {
        this.root = root;
        this.folder = folder;
    }

    private void createProject(File directory) {
        String projectName = getName(directory);
        logger.info("Creating project " + projectName);
        projectService.createProject(projectName);
    }

    private void createDocument(File directory) {
        String documentName = getName(directory);
        String projectName = directory.getParentFile().getName();
        logger.info("Creating document " + documentName);
        projectService.createDocument(projectName, documentName);
    }

    private String getName(File directory) {
        return directory.getPath().substring(directory.getParent().length() + 1);
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
