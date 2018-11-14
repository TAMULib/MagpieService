package edu.tamu.app.observer;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.exception.DocumentNotFoundException;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Destination;

public class HeadlessDocumentListener extends AbstractDocumentListener {

    private static final Logger logger = Logger.getLogger(HeadlessDocumentListener.class);

    @Value("${app.document.create.wait:10000}")
    private int documentCreationWait;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    public HeadlessDocumentListener(String root, String folder) {
        super(root, folder);
    }

    @Override
    public void onFileCreate(File file) {
        logger.debug("File listener not invoking resource creation for file " + file.getName() + " because we're waiting for directory quiescence in Headless mode.");
    }

    @Override
    protected void initializePendingResources(String documentName) {

    }

    @Override
    protected Document createDocument(File directory) {
        Document document = null;
        try {
            waitOnDirectory(directory);
            document = documentFactory.createDocument(directory);
            IngestType ingestType = document.getProject().getIngestType();
            if (!ingestType.equals(IngestType.SAF)) {
                document = processResources(document, directory);
                document = applyDefaultValues(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    @Override
    protected Document processResources(Document document, File directory) {
        // For the Headless use case, we can simply list the files in the document directory after it has become quiescent
        for (File resourceFile : directory.listFiles()) {
            try {
                if (!resourceFile.isHidden() && !resourceFile.isDirectory()) {
                    addResource(resourceFile);
                }
            } catch (DocumentNotFoundException e) {
                logger.error("Couldn't find a newly created file " + resourceFile.getName());
                e.printStackTrace();
            }
        }
        return document;
    }

    @Override
    protected void createdDocumentCallback(Document document) {
        if (document != null) {
            logger.info("Document created: " + document.getName());
            publishToRepositories(document);
        } else {
            logger.warn("Unable to create document!");
        }
    }

    // this is a blocking sleep operation of this listener
    private void waitOnDirectory(File directory) {
        logger.info("Waiting for directory " + directory + " to be quiescent, as it is a Headless project.");
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

    private Document applyDefaultValues(Document document) {
        for (MetadataFieldGroup mfg : document.getFields()) {
            MetadataFieldValue mfv = new MetadataFieldValue();
            mfv.setValue(mfg.getLabel().getProfile().getDefaultValue());
            mfg.addValue(mfv);
        }
        return documentRepo.save(document);
    }

    private void publishToRepositories(Document document) {
        logger.info("Attempting to publish document: " + document.getName());
        for (ProjectRepository repository : document.getProject().getRepositories()) {
            try {
                ((Destination) projectServiceRegistry.getService(repository.getName())).push(document);
            } catch (IOException e) {
                logger.error("Exception thrown attempting to push to " + repository.getName() + "!", e);
                e.printStackTrace();
            }
        }
    }

}
