package edu.tamu.app.observer;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Project;
import edu.tamu.app.service.ProjectFactory;
import edu.tamu.app.utilities.FileSystemUtility;

public class ProjectListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(ProjectListener.class);

    @Autowired
    private ProjectFactory projectFactory;

    @Autowired
    private FileObserverRegistry fileObserverRegistry;

    public ProjectListener(String root, String folder) {
        super(root, folder);
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {
        if (FileSystemUtility.getWindowsSafePath(directory.getParent()).equals(FileSystemUtility.getWindowsSafePath(getPath()))) {
            logger.info("Creating project: " + directory.getName());
            Project project = projectFactory.getOrCreateProject(directory);
            if (project.isHeadless()) {
                logger.info("Registering headless document listener: " + directory.getPath());
                fileObserverRegistry.register(new HeadlessDocumentListener(directory.getParent(), directory.getName()));
            } else {
                logger.info("Registering standard document listener: " + directory.getPath());
                fileObserverRegistry.register(new StandardDocumentListener(directory.getParent(), directory.getName()));
            }
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
