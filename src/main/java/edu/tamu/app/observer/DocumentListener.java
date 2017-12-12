package edu.tamu.app.observer;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.enums.IngestType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.service.DocumentFactory;

public class DocumentListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(DocumentListener.class);

    private static ExecutorService executor = null;

    @Value("${app.document.create.wait}")
    private int documentCreationWait;

    @Autowired
    private DocumentFactory documentFactory;

    private Project project;

    public DocumentListener(Project project, String root, String folder) {
        super(root, folder);
        this.project = project;
        executor = Executors.newFixedThreadPool(10);
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {
        logger.info("Createing document: " + directory.getName());
        createDocument(directory);
    }

    @Override
    public void onDirectoryChange(File directory) {

    }

    @Override
    public void onDirectoryDelete(File directory) {

    }

    @Override
    public void onFileCreate(File file) {
        if (!file.isHidden() && file.isFile()) {
            String documentName = file.getParentFile().getName();
            String projectName = project.getName();

            IngestType ingestType = project.getIngestType();

            if (!ingestType.equals(IngestType.SAF)) {
                executor.submit(() -> {
                    logger.info("Adding file " + file.getName() + " to " + documentName + " in project " + projectName);
                    Document document = documentFactory.getOrCreateDocument(projectName, documentName);
                    documentFactory.addResource(document, file);
                });
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

    private void createDocument(File directory) {
        if (waitOnDirectoryReady()) {
            executor.submit(() -> {
                waitOnDirectory(directory);
                documentFactory.getOrCreateDocument(directory);
            });
        }
    }

    // this is a blocking sleep operation of this listener
    private void waitOnDirectory(File directory) {
        System.out.println("Waiting for directory " + directory + " to be quiescent, as it is a Headless or SAF-ingest project.");
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
    }

    private boolean waitOnDirectoryReady() {
        return project.isHeadless() || project.getIngestType().equals(IngestType.SAF);
    }

}