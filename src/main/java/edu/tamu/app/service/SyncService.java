package edu.tamu.app.service;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
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
    private DocumentRepo documentRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectFactory projectFactory;

    @Autowired
    private DocumentFactory documentFactory;

    public SyncService() {

    }

    /**
     * Synchronizes the project directory with the database for a single project.
     *
     * @param Long projectId
     *   The ID of the specific project to synchronize.
     *
     * @throws IOException
     */
    public void sync(Long projectId) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Running Sync Service for ID " + projectId);
        }

        projectFactory.startProjectFileListeners();

        Project project = projectRepo.getOne(projectId);

        if (project != null) {
            String projectPath = ASSETS_PATH + File.separator + "projects" + File.separator + project.getName();
            logger.info("Found project: " + project.getName());

            if (project.isHeadless()) {
                logger.info(project.getName() + " is headless. Headless projects do not support manual sync!");
            } else {
                List<Path> documents = FileSystemUtility.directoryList(projectPath.toString());

                documents.parallelStream().forEach(documentPath -> {

                    logger.info("Found document: " + documentPath);

                    String documentName = documentPath.getFileName().toString();

                    Document document = documentRepo.findByProjectNameAndName(project.getName(), documentName);

                    try {
                        if (document == null) {
                            document = documentFactory.createDocument(documentPath.toFile());
                            if (!project.getIngestType().equals(IngestType.SAF)) {

                                List<Path> resources = FileSystemUtility.fileList(documentPath.toString());

                                for (Path resourcePath : resources) {

                                    logger.info("Found resource: " + resourcePath);

                                    File file = resourcePath.toFile();
                                    if (file.isFile() && !file.isHidden()) {
                                        documentFactory.addResource(document, file);
                                    }

                                }

                            } else {
                                logger.info("SAF ingest type cannot sync resources at this time. Please use listener.");
                            }
                        }

                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }

                });
            }

        }

        logger.info("Sync Service Finished for ID " + projectId);
    }

}
