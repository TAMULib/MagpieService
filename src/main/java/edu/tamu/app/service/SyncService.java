/* 
 * SyncService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Project;
import edu.tamu.app.utilities.FileSystemUtility;

/**
 * Sync Service. Synchronizes project database with projects folders.
 * 
 * @author
 */
@Service
public class SyncService {

    private static final Logger logger = Logger.getLogger(SyncService.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ProjectFactory projectFactory;

    @Autowired
    private DocumentFactory documentFactory;

    @Value("${app.mount}")
    private String mount;

    public SyncService() {

    }

    public void sync() throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Running Sync Service");
        }

        String directory = null;
        try {
            directory = FileSystemUtility.getWindowsSafePathString(resourceLoader.getResource("classpath:static" + mount).getURL().getPath()) + "/projects/";
        } catch (IOException e) {
            logger.error("Unable to load projects directory!");
            throw e;
        }

        List<Path> projects = FileSystemUtility.directoryList(directory);

        for (Path projectPath : projects) {

            String projectName = projectPath.getFileName().toString();

            Project project = projectFactory.getOrCreateProject(projectName);

            List<Path> documents = FileSystemUtility.fileList(projectPath.toString());

            documents.parallelStream().forEach(documentPath -> {

                String documentName = documentPath.getFileName().toString();

                documentFactory.createDocument(project, documentName);

            });
        }
        logger.info("Sync Service Finished");
    }

}
