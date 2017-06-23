package edu.tamu.app.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.PublishedLocation;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.utilities.CsvUtility;

@Service
public class MapsService {

    private static final Logger logger = Logger.getLogger(MapsService.class);

    // TODO: MapService needs to be scoped to a project
    private static final String DISSERTATION_PROJECT_NAME = "taes_misc_publication";

    // TODO: MapService needs to be scoped to a project and repository
    private static final String DISSERTATION_DSPACE_REPOSITORY_NAME = "DSpace for TAES Misc Publications";

    private static final String CHANGE_STATUS = "Published";

    @Value("${app.mount}")
    private String mount;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    private CsvUtility csvUtility;

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

        logger.info("Reading mapfile: " + file);

        Project dissertationProject = projectRepo.findByName(DISSERTATION_PROJECT_NAME);

        ProjectRepository projectRepository = getProjectRepository(dissertationProject);

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

            Document updateDoc = documentRepo.findByProjectNameAndName(DISSERTATION_PROJECT_NAME, documentName);

            if (updateDoc != null) {
                updateDoc.setStatus(CHANGE_STATUS);

                // TODO: improve method of retrieving project configurations
                String publishedUrl = updateDoc.getProject().getRepositories().get(0).getSettingValues("repoUrl") + "/" + documentHandle;

                updateDoc.addPublishedLocation(new PublishedLocation(projectRepository, publishedUrl));

                documentRepo.save(updateDoc);

                logger.info("Setting status of Document: " + updateDoc.getName() + " to " + CHANGE_STATUS);
            } else {
                logger.info("No Document found for string: " + documentName);
            }
        }

        unlockProject(dissertationProject);

        bReader.close();
        sReader.close();
        stream.close();

        if (file.delete()) {
            logger.info("Mapfile: " + file.getName() + " removed.");
        } else {
            logger.info("Error removing mapfile: " + file.getName() + ".");
        }
    }

    private ProjectRepository getProjectRepository(Project project) {
        Optional<ProjectRepository> dspaceRepository = Optional.empty();

        for (ProjectRepository repository : project.getRepositories()) {
            if (repository.getType().equals(ServiceType.DSPACE) && repository.getName().equals(DISSERTATION_DSPACE_REPOSITORY_NAME)) {
                dspaceRepository = Optional.of(repository);
                break;
            }
        }

        if (!dspaceRepository.isPresent()) {
            throw new RuntimeException("Could not find " + DISSERTATION_DSPACE_REPOSITORY_NAME + " DSpace repository!");
        }
        return dspaceRepository.get();
    }

    private void unlockProject(Project project) throws IOException {
        List<Document> unpublishedDocs = documentRepo.findByProjectNameAndStatus(DISSERTATION_PROJECT_NAME, "Pending");
        // unlock project if there are no pending documents
        if (unpublishedDocs.size() == 0) {
            // get the project fresh so the documents we modified above keep their changes
            project = projectRepo.findByName(DISSERTATION_PROJECT_NAME);
            project.setIsLocked(false);
            project = projectRepo.save(project);
            logger.info("Project '" + project.getName() + "' unlocked.");
            generateArchiveMaticaCSV(project.getName());
        } else {
            logger.info("Project '" + DISSERTATION_PROJECT_NAME + "' was left locked because there was a count of  " + unpublishedDocs.size() + " unpublished document(s).");
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
