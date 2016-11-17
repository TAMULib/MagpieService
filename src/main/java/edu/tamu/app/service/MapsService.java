package edu.tamu.app.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.utilities.CsvUtility;

@Service
public class MapsService {

    private static final Logger logger = Logger.getLogger(MapsService.class);
    
    // TODO: MapService needs to be scoped to a project
    private static final String DISSERTATION_PROJECT_NAME = "dissertation";

    private static final String CHANGE_STATUS = "Published";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private CsvUtility csvUtility;

    @Value("${app.mount}")
    private String mount;

    public MapsService() {

    }

    public void readMapFile(File file) throws IOException {
        // read and iterate over mapfile
        if (logger.isDebugEnabled()) {
            logger.debug("The map file is named: " + file.getName());
        }
        InputStream stream = new FileInputStream(file);
        InputStreamReader sReader = new InputStreamReader(stream);
        BufferedReader bReader = new BufferedReader(sReader);

        // the project to unlock, if all documents have been published
        String unlockableProjectName = null;
        logger.info("Reading mapfile: " + file);

        String line;
        while ((line = bReader.readLine()) != null) {
            String[] itemData = line.split(" ");

            // extract document name from mapfile row
            String documentName = itemData[0];

            // extract document handle from mapfile row
            String documentHandle = itemData[1];

            if (logger.isDebugEnabled()) {
                logger.debug("The document name is: " + documentName);
            }

            Document updateDoc = documentRepo.findByProjectNameAndName(DISSERTATION_PROJECT_NAME, documentName);

            if (updateDoc != null) {
                if (unlockableProjectName == null) {
                    unlockableProjectName = updateDoc.getProject().getName();
                }
                updateDoc.setStatus(CHANGE_STATUS);
                updateDoc.setPublishedUriString(updateDoc.getProject().getRepositoryUrlString() + "/" + documentHandle);
                documentRepo.save(updateDoc);
                logger.info("Setting status of Document: " + updateDoc.getName() + " to " + CHANGE_STATUS);
            } else {
                logger.info("No Document found for string: " + documentName);
            }
        }
        if (unlockableProjectName != null) {
            List<Document> unpublishedDocs = documentRepo.findByProjectNameAndStatus(unlockableProjectName, "Pending");
            // unlock project if there are no pending documents
            if (unpublishedDocs.size() == 0) {
                // get the project fresh so the documents we modified above keep their changes
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
        if (file.delete()) {
            logger.info("Mapfile: " + file.getName() + " removed.");
        } else {
            logger.info("Error removing mapfile: " + file.getName() + ".");
        }
    }

    private void generateArchiveMaticaCSV(String projectName) throws IOException {
        logger.info("Writing Archivematica CSV for: " + projectName);
        String directory = resourceLoader.getResource("classpath:static" + mount).getURL().getPath() + "/archivematica/";

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
