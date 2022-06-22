package edu.tamu.app.observer;

import static edu.tamu.app.Initialization.ASSETS_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.utilities.FileSystemUtility;

@ActiveProfiles("test")
@SpringBootTest(classes = WebServerInit.class)
public class ProjectListenerTest {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Test
    public void testListeners() throws IOException, InterruptedException {
        String projectsPath = ASSETS_PATH + File.separator + "projects";
        String testsPath = projectsPath + File.separator + "tests";
        FileSystemUtility.createDirectory(testsPath);

        // wait for the file monitor to pick up the newly created directory
        Thread.sleep(2500);

        assertEquals(1, projectRepo.count(), "The project repo has the incorrect number of projects!");
        assertNotNull(projectRepo.findByName("tests"), "The tests project was not created!");

        String documentPath = testsPath + File.separator + "test_0";
        FileSystemUtility.createDirectory(documentPath);
        FileSystemUtility.createFile(documentPath, "test.pdf");
        FileSystemUtility.createFile(documentPath, "test.pdf.txt");

        // wait for the file monitor to pick up the newly created directory and files
        Thread.sleep(2500);

        assertEquals(1, documentRepo.count(), "The document repo has the incorrect number of documents!");
        assertNotNull(documentRepo.findByProjectNameAndName("tests", "test_0"), "The test_0 document was not created!");

        assertEquals(2, resourceRepo.count(), "The resource repo has the incorrect number of resources!");

        assertNotNull(resourceRepo.findByDocumentProjectNameAndDocumentNameAndName("tests", "test_0", "test.pdf"), "The test.pdf resource was not created!");
        assertNotNull(resourceRepo.findByDocumentProjectNameAndDocumentNameAndName("tests", "test_0", "test.pdf.txt"), "The test.pdf.txt resource was not created!");

        FileSystemUtility.deleteDirectory(testsPath);
    }

    @AfterEach
    public void cleanUp() throws Exception {
        resourceRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
        fieldProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
    }
}
