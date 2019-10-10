package edu.tamu.app.observer;

import static edu.tamu.app.Initialization.ASSETS_PATH;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
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

    private File directory;

    @Before
    public void setUp() {
        directory = new File(ASSETS_PATH + File.separator + "temp");
    }

    @Test
    public void testStart() throws Exception {
        assertEquals(2, registry.getObservers().size());

        String projectsPath = ASSETS_PATH + File.separator + "projects";
        String testsPath = projectsPath + File.separator + "tests";
        FileSystemUtility.createDirectory(testsPath);

        // wait for the file monitor to pick up the newly created directory
        Thread.sleep(2500);

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
        FileSystemUtility.createDirectory(directory.getAbsolutePath());

        registry.register(new StandardDocumentListener(directory.getParent(), directory.getName()));

        Thread.sleep(1000);

        assertEquals(3, registry.getObservers().size());
    }

    @Test
    public void testDismiss() throws Exception {
        testRegister();

        registry.dismiss(directory.getAbsolutePath());

        Thread.sleep(1000);

        assertEquals(2, registry.getObservers().size());
    }

    @Test
    public void testHealthCheck() throws Exception {
        registry.healthCheck();
    }

    @After
    public void cleanUp() throws IOException {
        if (directory.exists()) {
            FileSystemUtility.deleteDirectory(directory.getAbsolutePath());
        }
        resourceRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
        fieldProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
    }

}
