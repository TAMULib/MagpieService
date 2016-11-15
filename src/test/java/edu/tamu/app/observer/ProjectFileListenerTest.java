    package edu.tamu.app.observer;
    
    import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.ProjectsService;
import edu.tamu.app.utilities.FileSystemUtility;
    
    @WebAppConfiguration
    @ActiveProfiles({ "test" })
    @RunWith(OrderedRunner.class)
    @SpringApplicationConfiguration(classes = WebServerInit.class)
    public class ProjectFileListenerTest {
    
        @Value("${app.mount}")
        private String mount;
    
        @Autowired
        private ResourceLoader resourceLoader;
    
        @Autowired
        private FileMonitorManager fileMonitorManager;
    
        @Autowired
        private FileObserverRegistry fileObserverRegistry;
    
        @Autowired
        private ProjectsService projectsService;
    
        @Autowired
        private ProjectRepo projectRepo;
    
        @Autowired
        private DocumentRepo documentRepo;
    
        @Autowired
        private FieldProfileRepo fieldProfileRepo;
    
        @Autowired
        private MetadataFieldGroupRepo metadataFieldGroupRepo;
    
        @Autowired
        private MetadataFieldLabelRepo metadataFieldLabelRepo;
    
        @Autowired
        private MetadataFieldValueRepo metadataFieldValueRepo;
    
        private ProjectFileListener dissertationFileListener;
    
        private String projectsPath;
    
        @Before
        public void setup() throws Exception {
            String mountPath = FileSystemUtility.getWindowsSafePathString(resourceLoader.getResource("classpath:static" + mount).getURL().getPath());
            projectsPath = mountPath + "/projects";
            FileSystemUtility.createDirectory(projectsPath);
            dissertationFileListener = new ProjectFileListener(mountPath, "projects");
            fileObserverRegistry.register(dissertationFileListener);
            fileMonitorManager.start();

            assertEquals("The project repo has the incorrect number of projects!", 0, projectRepo.count());
        }
    
        @Test
        @Order(1)
        public void testNewProject() throws IOException, InterruptedException {
            String disseratationsPath = projectsPath + "/dissertation";
            FileSystemUtility.createDirectory(disseratationsPath);
    
            // wait for the file monitor to pick up the newly created directory
            Thread.sleep(2500);
    
            assertEquals("The project repo has the incorrect number of projects!", 1, projectRepo.count());
        }
    
        @Test
        @Order(2)
        public void testNewDocument() throws IOException, InterruptedException {
            String disseratationsPath = projectsPath + "/dissertation";
            String documentPath = disseratationsPath + "/dissertation_0";

            FileSystemUtility.createDirectory(disseratationsPath);
            FileSystemUtility.createDirectory(documentPath);
            FileSystemUtility.createFile(documentPath, "dissertation.pdf");
            FileSystemUtility.createFile(documentPath, "dissertation.pdf.txt");
    
            // wait for the file monitor to pick up the newly created directory and files
            Thread.sleep(2500);
    
            assertEquals("The project repo has the incorrect number of projects!", 1, projectRepo.count());
            assertNotNull("The dissertation project was not created!", projectRepo.findByName("dissertation"));
    
            assertEquals("The document repo has the incorrect number of documents!", 1, documentRepo.count());
            assertNotNull("The dissertation_0 document was not created!", documentRepo.findByName("dissertation_0"));
        }
    
        @After
        public void cleanUp() throws Exception {
            fieldProfileRepo.deleteAll();
            metadataFieldValueRepo.deleteAll();
            metadataFieldLabelRepo.deleteAll();
            metadataFieldGroupRepo.deleteAll();
            documentRepo.deleteAll();
            projectRepo.deleteAll();
            projectsService.clear();
            fileObserverRegistry.dismiss(dissertationFileListener);
            fileMonitorManager.stop();
    
            FileSystemUtility.deleteDirectory(projectsPath);
        }
    }
