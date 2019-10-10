package edu.tamu.app.observer;

import static edu.tamu.app.Initialization.ASSETS_PATH;
import static edu.tamu.app.Initialization.PROJECTS_PATH;
import static org.junit.Assert.assertEquals;

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
public class FileObserverRegistryTest {

    @Autowired
    private FileObserverRegistry registry;

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
    public void testStart() throws Exception {
        String projectsPath = ASSETS_PATH + File.separator + PROJECTS_PATH;
        String testsPath = projectsPath + File.separator + "tests";

        assertEquals(2, registry.getObservers().size());

        FileSystemUtility.createDirectory(testsPath);

        // wait for the file monitor to pick up the newly created directory
        Thread.sleep(1500);

        assertEquals(3, registry.getObservers().size());

        FileSystemUtility.deleteDirectory(testsPath);
    }

    @Test
    public void testStop() throws Exception {
        registry.stop();
        assertEquals(0, registry.getObservers().size());
        registry.start();
        assertEquals(2, registry.getObservers().size());
    }

    @Test
    public void testRestart() throws Exception {
        registry.restart();
        assertEquals(2, registry.getObservers().size());
    }

    @Test
    public void testRegister() throws Exception {
        String tempPath = ASSETS_PATH + File.separator + "temp";

        FileSystemUtility.createDirectory(tempPath);

        File directory = new File(tempPath);

        registry.register(new StandardDocumentListener(directory.getParent(), directory.getName()));

        assertEquals(3, registry.getObservers().size());

        FileSystemUtility.deleteDirectory(tempPath);
    }

    @Test
    public void testDismiss() throws Exception {
        testRegister();
        String tempPath = ASSETS_PATH + File.separator + "temp";

        File directory = new File(tempPath);

        registry.dismiss(directory.getAbsolutePath());

        assertEquals(2, registry.getObservers().size());
    }

    @Test
    public void testHealthCheck() throws Exception {
        registry.healthCheck();
    }

    @After
    public void cleanUp() throws IOException {
        resourceRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
        fieldProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
    }

}
