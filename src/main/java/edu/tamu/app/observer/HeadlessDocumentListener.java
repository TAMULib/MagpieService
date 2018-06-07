package edu.tamu.app.observer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;

public class HeadlessDocumentListener extends AbstractDocumentListener {

    private static final Logger logger = Logger.getLogger(HeadlessDocumentListener.class);

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    @Value("${app.document.create.wait}")
    private int documentCreationWait;

    public HeadlessDocumentListener(String root, String folder) {
        super(root, folder);
    }

    @Override
    protected CompletableFuture<Document> createDocument(File directory) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = null;
            try {
                waitOnDirectory(directory);
                document = documentFactory.createDocument(directory);
                document = processPendingResources(document);
                document = applyDefaultValues(document);
                logger.info("Document created: " + document.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return document;
        }, executor);
    }

    @Override
    protected void createdDocumentCallback(Document document) {
        if (document != null) {
            publishToRepositories(document);
        } else {
            logger.warn("Unable to create document!");
        }
    }

    @Override
    protected void addResource(File file) {
        String documentName = file.getParentFile().getName();
        List<String> documentPendingResources = pendingResources.get(documentName);
        documentPendingResources.add(file.getAbsolutePath());
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
                ((Repository) projectServiceRegistry.getService(repository.getName())).push(document);
            } catch (IOException e) {
                logger.error("Exception thrown attempting to push to " + repository.getName() + "!", e);
                e.printStackTrace();
            }
        }
    }

}
