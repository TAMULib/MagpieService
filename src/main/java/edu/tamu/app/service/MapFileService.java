package edu.tamu.app.service;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.PublishedLocation;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.registry.MagpieAuxiliaryService;
import edu.tamu.app.utilities.CsvUtility;

@Service
@Scope("prototype")
public class MapFileService implements MagpieAuxiliaryService {

    private static final Logger logger = Logger.getLogger(MapFileService.class);

    private static final String CHANGE_STATUS = "Published";

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    private ProjectRepository projectRepository;

    private Project project;

    private CsvUtility csvUtility;

    public MapFileService(Project project, ProjectRepository projectRepository) {
        this.project = project;
        this.projectRepository = projectRepository;
    }

    @PostConstruct
    public void init() {
        logger.info("Creating project map directory: " + project.getName());
        String mapDirectoryName = String.join(File.separator, ASSETS_PATH, "maps", project.getName());

        if (logger.isDebugEnabled()) {
            logger.debug("Project map directory: " + mapDirectoryName);
        }

        File mapDirectory = new File(mapDirectoryName);
        mapDirectory.mkdir();
    }

    public void readMapFile(File file) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("The map file is named: " + file.getName());
        }

        InputStream stream = new FileInputStream(file);
        InputStreamReader sReader = new InputStreamReader(stream);
        BufferedReader bReader = new BufferedReader(sReader);

        logger.info("Reading mapfile: " + file);

        project = projectRepo.findByName(project.getName());

        csvUtility = new CsvUtility(projectRepository);

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

            Document updateDoc = documentRepo.findByProjectNameAndName(project.getName(), documentName);

            if (updateDoc != null) {
                updateDoc.setStatus(CHANGE_STATUS);

                // TODO: improve method of retrieving project configurations
                String publishedUrl = String.join("/", updateDoc.getProject().getRepositories().get(0).getSettingValues("repoUrl").get(0), updateDoc.getProject().getRepositories().get(0).getSettingValues("repoContextPath").get(0), documentHandle);

                updateDoc.addPublishedLocation(new PublishedLocation(projectRepository, publishedUrl));

                updateDoc = documentRepo.update(updateDoc);

                logger.info("Setting status of Document: " + updateDoc.getName() + " to " + CHANGE_STATUS);
            } else {
                logger.info("No Document found for string: " + documentName);
            }
        }

        unlockProject(project);

        bReader.close();
        sReader.close();
        stream.close();

        if (file.delete()) {
            logger.info("Mapfile: " + file.getName() + " removed.");
        } else {
            logger.info("Error removing mapfile: " + file.getName() + ".");
        }
    }

    private void unlockProject(Project project) throws IOException {
        List<Document> unpublishedDocs = documentRepo.findByProjectNameAndStatus(project.getName(), "Pending");
        // unlock project if there are no pending documents
        if (unpublishedDocs.size() == 0) {
            // get the project fresh so the documents we modified above keep their changes
            project = projectRepo.findByName(project.getName());
            project.setLocked(false);
            project = projectRepo.save(project);
            logger.info("Project '" + project.getName() + "' unlocked.");
            generateArchiveMaticaCSV(project.getName());
        } else {
            logger.info("Project '" + project.getName() + "' was left locked because there was a count of  " + unpublishedDocs.size() + " unpublished document(s).");
        }
    }

    private void generateArchiveMaticaCSV(String projectName) throws IOException {
        logger.info("Writing Archivematica CSV for: " + projectName);
        String directory = ASSETS_PATH + File.separator + "archivematica";

        String archiveDirectoryName = directory + File.separator + projectName;

        List<Document> documents = documentRepo.findByProjectNameAndStatus(projectName, "Published");

        File archiveDirectory = new File(archiveDirectoryName);
        if (archiveDirectory.isDirectory() == false) {
            archiveDirectory.mkdir();
        }

        for (Document document : documents) {
            String itemDirectoryName = archiveDirectoryName + File.separator + document.getName();
            csvUtility.generateOneArchiveMaticaCSV(document, itemDirectoryName);
        }
    }

}
