package edu.tamu.app.observer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.exception.DocumentNotFoundException;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.DocumentFactory;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;

public abstract class AbstractDocumentListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(AbstractDocumentListener.class);

    protected static final Map<String, List<String>> pendingResources = new ConcurrentHashMap<String, List<String>>();

    protected static final ExecutorService executor = Executors.newFixedThreadPool(10);

    private static final List<Document> publishQueue = new CopyOnWriteArrayList<Document>();

    private static final AtomicInteger publishing = new AtomicInteger(0);

    @Value("${app.document.publish.concurrency:5}")
    private int publishConcurrency;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    protected DocumentFactory documentFactory;

    @Autowired
    protected DocumentRepo documentRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

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
            createDocument(directory).thenAccept(newDocument -> {
                if (newDocument != null) {
                    processPendingResources(newDocument);
                    publishToRepositories(newDocument);
                } else {
                    logger.warn("Unable to create document!");
                }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return document;
        }, executor);
    }

    protected void addResource(File file) throws DocumentNotFoundException {
        documentFactory.addResource(file);
    }

    private void publishToRepositories(Document document) {

        if (publishing.get() <= publishConcurrency) {
            publishing.incrementAndGet();

            for (ProjectRepository repository : document.getProject().getRepositories()) {
                try {
                    document = ((Repository) projectServiceRegistry.getService(repository.getName())).push(document);
                } catch (IOException e) {
                    logger.error("Exception thrown attempting to push to " + repository.getName() + "!", e);
                    e.printStackTrace();
                }
            }

            publishing.decrementAndGet();

            if (publishQueue.size() > 0) {
                Document queuedDocument = publishQueue.get(0);
                publishQueue.remove(0);
                publishToRepositories(queuedDocument);
            }

        } else {
            publishQueue.add(document);
        }

    }

}
