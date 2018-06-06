package edu.tamu.app.observer;

import static edu.tamu.app.Initialization.ASSETS_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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
@RunWith(SpringRunner.class)
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
        String disseratationsPath = projectsPath + File.separator + "test";
        FileSystemUtility.createDirectory(disseratationsPath);

        // wait for the file monitor to pick up the newly created directory
        Thread.sleep(2500);

        assertEquals("The project repo has the incorrect number of projects!", 1, projectRepo.count());
        assertNotNull("The test project was not created!", projectRepo.findByName("test"));

        String documentPath = disseratationsPath + File.separator + "test_0";
        FileSystemUtility.createDirectory(documentPath);
        FileSystemUtility.createFile(documentPath, "test.pdf");
        FileSystemUtility.createFile(documentPath, "test.pdf.txt");

        // wait for the file monitor to pick up the newly created directory and files
        Thread.sleep(2500);

        assertEquals("The document repo has the incorrect number of documents!", 1, documentRepo.count());
        assertNotNull("The test_0 document was not created!", documentRepo.findByProjectNameAndName("test", "test_0"));

        assertEquals("The resource repo has the incorrect number of resources!", 2, resourceRepo.count());

        assertNotNull("The test.pdf resource was not created!", resourceRepo.findByDocumentProjectNameAndDocumentNameAndName("test", "test_0", "test.pdf"));
        assertNotNull("The test.pdf.txt resource was not created!", resourceRepo.findByDocumentProjectNameAndDocumentNameAndName("test", "test_0", "test.pdf.txt"));

        FileSystemUtility.deleteDirectory(disseratationsPath);
        FileSystemUtility.deleteDirectory(projectsPath);
    }

    @After
    public void cleanUp() throws Exception {
        fieldProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        resourceRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
    }
}
