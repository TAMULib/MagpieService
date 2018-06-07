package edu.tamu.app.observer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.exception.DocumentNotFoundException;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.DocumentFactory;

public abstract class AbstractDocumentListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(AbstractDocumentListener.class);

    protected static final Map<String, List<String>> pendingResources = new ConcurrentHashMap<String, List<String>>();

    protected static final ExecutorService executor = Executors.newFixedThreadPool(10);

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    protected DocumentFactory documentFactory;

    @Autowired
    protected DocumentRepo documentRepo;

    public AbstractDocumentListener(String root, String folder) {
        super(root, folder);
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {
        Document existingDocument = documentRepo.findByProjectNameAndName(directory.getParentFile().getName(), directory.getName());
        if (existingDocument == null) {
            initializePendingResources(directory.getName());
            createDocument(directory).thenAccept(document -> {
                createdDocumentCallback(document);
            });
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
        if (!file.isHidden() && file.isFile()) {
            logger.debug("File created: " + file.getName());
            String projectName = file.getParentFile().getParentFile().getName();
            Project parentProject = projectRepo.findByName(projectName);
            if (!parentProject.getIngestType().equals(IngestType.SAF)) {
                try {
                    addResource(file);
                } catch (DocumentNotFoundException e) {
                    String documentName = file.getParentFile().getName();
                    {
                        logger.debug("Document " + documentName + " not yet created for resource. Adding to pending resources.");
                        List<String> documentPendingResources = pendingResources.get(documentName);
                        documentPendingResources.add(file.getAbsolutePath());
                    }
                }
            } else {
                logger.debug("Ignoring file create of " + file.getName() + " becuase project is SAF and we will use the contents manifest to add resources.");
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

    protected void initializePendingResources(String documentName) {
        pendingResources.put(documentName, new ArrayList<String>());
    }

    protected Document processPendingResources(Document document) {
        String documentName = document.getName();
        for (String resourcePath : pendingResources.get(documentName)) {
            document = documentFactory.addResource(document, new File(resourcePath));
        }
        pendingResources.remove(documentName);
        return document;
    }

    protected CompletableFuture<Document> createDocument(File directory) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = null;
            try {
                document = documentFactory.createDocument(directory);
                document = processPendingResources(document);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return document;
        }, executor);
    }

    protected void createdDocumentCallback(Document document) {
        if (document != null) {
            logger.info("Document created: " + document.getName());
        } else {
            logger.warn("Unable to create document!");
        }
    }

    protected void addResource(File file) throws DocumentNotFoundException {
        documentFactory.addResource(file);
    }

}
