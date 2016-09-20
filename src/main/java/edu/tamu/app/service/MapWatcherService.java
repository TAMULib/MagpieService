/* 
 * WatcherService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.utilities.CsvUtility;

/**
 * Watches map file folder, harvests contents, and updates app data as needed
 * 
 * @author
 *
 */
@Service
@Scope(value = "prototype")
public class MapWatcherService implements Runnable {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private CsvUtility csvUtility;

    @Value("${app.host}")
    private String host;

    @Value("${app.mount}")
    private String mount;

    @Value("${app.symlink.create}")
    private String link;

    private String folder;

    private static final Logger logger = Logger.getLogger(MapWatcherService.class);

    /**
     * Default constructor.
     * 
     */
    public MapWatcherService() {
        super();
    }

    public MapWatcherService(String folder) {
        super();
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    /**
     * WatcherService runnable.
     * 
     */
    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        String directory = "";
        try {
            directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/" + folder;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = FileSystems.getDefault().getPath(directory, "");
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            logger.info("MapWatch Service registered for dir: " + dir.getFileName());
            // the string representing the published state
            String changeStatus = "Published";
            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path file = ev.context();

                    logger.info(kind.name() + ": " + file);
                    if (kind == ENTRY_CREATE) {
                        String line;
                        try {
                            // read and iterate over mapfile
                            String mapFileName = directory + "/" + file.toFile();
                            if (logger.isDebugEnabled()) {
                                logger.debug("The map file is named: " + mapFileName);
                            }
                            InputStream stream = new FileInputStream(mapFileName);
                            InputStreamReader sReader = new InputStreamReader(stream);
                            BufferedReader bReader = new BufferedReader(sReader);
                            // the project to unlock, if all documents have been published
                            String unlockableProjectName = null;
                            logger.info("Reading mapfile: " + file);

                            while ((line = bReader.readLine()) != null) {
                                String[] itemData = line.split(" ");

                                // extract document name from mapfile row
                                String documentName = itemData[0];

                                // extract document handle from mapfile row
                                String documentHandle = itemData[1];

                                if (logger.isDebugEnabled()) {
                                    logger.debug("The document name is: " + documentName);
                                }

                                Document updateDoc = documentRepo.findByName(documentName);

                                if (updateDoc != null) {
                                    if (unlockableProjectName == null) {
                                        unlockableProjectName = updateDoc.getProject().getName();
                                    }
                                    updateDoc.setStatus(changeStatus);
                                    updateDoc.setPublishedUriString(updateDoc.getProject().getRepositoryUrlString() + "/" + documentHandle);
                                    documentRepo.save(updateDoc);
                                    logger.info("Setting status of Document: " + updateDoc.getName() + " to " + changeStatus);
                                } else {
                                    logger.info("No Document found for string: " + documentName);
                                }
                            }
                            if (unlockableProjectName != null) {
                                List<Document> unpublishedDocs = documentRepo.findByProjectNameAndStatus(unlockableProjectName, "Pending");
                                // unlock project if there are no pending documents
                                if (unpublishedDocs.size() == 0) {
                                    // get the project fresh so the documents we
                                    // modified above keep their changes
                                    Project unlockableProject = projectRepo.findByName(unlockableProjectName);
                                    unlockableProject.setIsLocked(false);
                                    projectRepo.save(unlockableProject);
                                    logger.info("Project '" + unlockableProject.getName() + "' unlocked.");
                                    generateArchiveMaticaCSV(unlockableProject.getName());
                                } else {
                                    logger.info("Project '" + unlockableProjectName + "' was left locked because there was a count of  " + unpublishedDocs.size() + " unpublished document(s).");
                                }
                            } else {
                                logger.info("No Project found");
                            }
                            bReader.close();
                            File mapFile = new File(mapFileName);
                            if (mapFile.delete()) {
                                logger.info("Mapfile: " + mapFileName + " removed.");
                            } else {
                                logger.info("Error removing mapfile: " + mapFileName + ".");
                            }
                        } catch (IOException e) {
                            logger.error(e);
                        }

                    }
                }
                if (!key.reset()) {
                    logger.info("Key reset invalid!");
                    break;
                }
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void generateArchiveMaticaCSV(String projectName) throws IOException {
        logger.info("Writing Archivematica CSV for: " + projectName);
        String directory = "";
        try {
            directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/archivematica/";
        } catch (IOException e) {
            e.printStackTrace();
        }

        String archiveDirectoryName = directory + projectName;

        List<Document> documents = documentRepo.findByProjectNameAndStatus(projectName, "Published");

        File archiveDirectory = new File(archiveDirectoryName);
        if (archiveDirectory.isDirectory() == false) {
            archiveDirectory.mkdir();
        }

        for (Document document : documents) {
            String itemDirectoryName = archiveDirectoryName + "/" + document.getName();
            csvUtility.generateOneArchiveMaticaCSV(document, itemDirectoryName);
        }

    }

}
