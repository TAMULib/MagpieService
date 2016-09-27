package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.After;
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

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.utilities.FileSystemUtility;

@WebAppConfiguration
@ActiveProfiles({ "test" })
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class ProjectsServiceTest {
    
    @Autowired
    private ResourceLoader resourceLoader;

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
    
    @Value("${app.mount}")
    private String mount;

    @Test
    @Order(1)
    public void testReadProjectNode() {
        JsonNode projectsNode = projectsService.readProjectNode();
        assertNotNull("The projects node was not read!", projectsNode);
    }

    @Test
    @Order(2)
    public void testCreateProject() {
        Project project = projectsService.createProject("dissertation");
        assertNotNull("The project was not created!", project);
        assertEquals("The project repo has the incorrect number of projects!", 1, projectRepo.count());
    }

    @Test
    @Order(3)
    public void testGetProfileNode() {
        JsonNode profileNode = projectsService.getProfileNode("dissertation");
        assertNotNull("The profile node was not retrieved!", profileNode);
    }

    @Test
    @Order(4)
    public void testGetProjectFields() {
        List<MetadataFieldGroup> projectFields = projectsService.getProjectFields("dissertation");
        assertEquals("The project fields did not have the correct number of MetadataFieldFroups!", projectFields.size(), 21);
    }

    @Test
    @Order(5)
    public void testCreateDocument() throws IOException {
        String projectPath = resourceLoader.getResource("classpath:static" + mount).getURL().getPath() + "/dissertation";
        String documentPath = projectPath + "/dissertation_0";
        FileSystemUtility.createDirectory(projectPath);
        FileSystemUtility.createDirectory(documentPath);
        FileSystemUtility.createFile(documentPath, "dissertation.pdf");
        FileSystemUtility.createFile(documentPath, "dissertation.pdf.txt");
        
        projectsService.createDocument("dissertation", "dissertation_0");
        
        assertEquals("The project repo has the incorrect number of projects!", 1, projectRepo.count());
        assertNotNull("The dissertation project was not created!", projectRepo.findByName("dissertation"));
        
        assertEquals("The document repo has the incorrect number of documents!", 1, documentRepo.count());
        assertNotNull("The dissertation_0 document was not created!", documentRepo.findByName("dissertation_0"));
    }

    @After
    public void cleanUp() {
        fieldProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
        projectsService.clear();
    }
}