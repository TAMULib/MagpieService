package edu.tamu.app.observer;

import static edu.tamu.app.Initialization.LISTENER_PARALLELISM;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private static final Map<String, List<String>> pendingResources = new ConcurrentHashMap<String, List<String>>();

    private static final ExecutorService parallelExecutor = Executors.newFixedThreadPool(LISTENER_PARALLELISM);

    private static final AtomicBoolean firstDocument = new AtomicBoolean(true);

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    protected DocumentRepo documentRepo;

    @Autowired
    protected DocumentFactory documentFactory;

    public AbstractDocumentListener(String root, String folder) {
        super(root, folder);
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {
        logger.info("onDirectoryCreate: " + directory.getName());
        Document existingDocument = documentRepo.findByProjectNameAndName(directory.getParentFile().getName(), directory.getName());
        if (existingDocument == null) {
            initializePendingResources(directory.getName());
            if (firstDocument.compareAndSet(true, false)) {
                Document document = createDocument(directory);
                createdDocumentCallback(document);
            } else {
                completableCreateDocument(directory).thenAccept(document -> {
                    createdDocumentCallback(document);
                });
            }
        }
    }

    @Override
    public void onDirectoryChange(File directory) {
        logger.info("onDirectoryChange: " + directory.getName());
    }

    @Override
    public void onDirectoryDelete(File directory) {
        logger.info("onDirectoryDelete: " + directory.getName());
    }

    @Override
    public void onFileCreate(File file) {
        logger.info("onFileCreate: " + file.getName());
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
        logger.info("onFileChange: " + file.getName());
    }

    @Override
    public void onFileDelete(File file) {
        logger.info("onFileDelete: " + file.getName());
    }

    @Override
    public void onStop(FileAlterationObserver observer) {

    }

    protected void initializePendingResources(String documentName) {
        pendingResources.put(documentName, new ArrayList<String>());
    }

    protected Document createDocument(File directory) {
        Document document = null;
        try {
            document = documentFactory.createDocument(directory);
            document = processResources(document, directory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    protected Document processResources(Document document, File directory) {
        String documentName = document.getName();
        for (String resourcePath : pendingResources.get(documentName)) {
            document = documentFactory.addResource(document, new File(resourcePath));
        }
        pendingResources.remove(documentName);
        return document;
    }

    protected void createdDocumentCallback(Document document) {
        if (document != null) {
            logger.info("Document created: " + document.getName());
        } else {
            logger.warn("Unable to create document!");
        }
    }

    protected void addResource(File file) throws DocumentNotFoundException {
        Instant start = Instant.now();
        documentFactory.addResource(file);
        logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to add resource");
    }

    private synchronized CompletableFuture<Document> completableCreateDocument(File directory) {
        return CompletableFuture.supplyAsync(() -> {
            return createDocument(directory);
        }, parallelExecutor);
    }

}
