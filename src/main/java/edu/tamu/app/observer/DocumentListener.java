package edu.tamu.app.observer;

import java.io.File;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.enums.IngestType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.service.DocumentFactory;

public class DocumentListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(DocumentListener.class);

    @Value("${app.document.create.wait}")
    private int documentCreationWait;

    @Value("${app.host}")
    private String host;

    @Value("${app.mount}")
    private String mount;

    @Autowired
    private DocumentFactory documentFactory;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    private Project project;

    private Tika tika;

    public DocumentListener(Project project, String root, String folder) {
        super(root, folder);
        this.project = project;
        this.tika = new Tika();
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
        String documentName = file.getParentFile().getName();
        String projectName = project.getName();

        IngestType ingestType = project.getIngestType();

        if (!ingestType.equals(IngestType.SAF)) {
            logger.info("Adding file " + file.getName() + " to " + documentName + " in project " + projectName);
            Document document = documentRepo.findByProjectNameAndName(projectName, documentName);
            addResource(document, file);
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
        if (directoryIsReady(directory)) {
            documentFactory.createDocument(directory);
        }
    }

    private void addResource(Document document, File file) {
        if (!file.isHidden() && file.isFile()) {
            String name = file.getName();
            String path = document.getDocumentPath() + File.separator + file.getName();
            String url = host + document.getDocumentPath() + File.separator + file.getName();
            String mimeType = tika.detect(path);
            logger.info("Adding resource " + name + " - " + mimeType + " to document " + document.getName());
            resourceRepo.create(document, name, path, url, mimeType);
        }
    }

    // this is a blocking sleep operation of this listener
    private boolean directoryIsReady(File directory) {
        boolean directoryReady = false;
        IngestType ingestType = project.getIngestType();
        if (project.isHeadless() || ingestType.equals(IngestType.SAF)) {
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
            directoryReady = true;
        } else {
            System.out.println("Directory " + directory + " of ingest type " + ingestType + " is neither Headless nor SAF-ingest; consider it immediately ready.");
            directoryReady = true;
        }

        return directoryReady;
    }

}
