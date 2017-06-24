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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

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

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebServerInit.class)
public class ProjectsServiceTest {

    @Value("${app.mount}")
    private String mount;

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

    @Test
    public void testReadProjectNode() {
        JsonNode projectsNode = projectsService.readProjectsNode();
        assertNotNull("The projects node was not read!", projectsNode);
    }

    @Test
    public void testGetProject() {
        Project project = projectsService.getOrCreateProject("default");
        assertNotNull("The project was not created!", project);
        assertEquals("The project repo has the incorrect number of projects!", 1, projectRepo.count());
        assertEquals("The project has the incorrect number of repositories!", 0, project.getRepositories().size());
        assertEquals("The project has the incorrect number of authorities!", 0, project.getAuthorities().size());
        assertEquals("The project has the incorrect number of suggestors!", 0, project.getSuggestors().size());
    }

    @Test
    public void testGetProjectNode() {
        JsonNode profileNode = projectsService.getProjectNode("default");
        assertNotNull("The profile node was not retrieved!", profileNode);
    }

    @Test
    public void testGetProjectFields() {
        List<MetadataFieldGroup> projectFields = projectsService.getProjectFields("default");
        assertEquals("The project fields did not have the correct number of MetadataFieldFroups!", projectFields.size(), 2);
    }

    @Test
    public void testCreateDocument() throws IOException {
        projectsService.createDocument("default", "default");

        assertEquals("The project repo has the incorrect number of projects!", 1, projectRepo.count());
        assertNotNull("The default project was not created!", projectRepo.findByName("default"));

        assertEquals("The document repo has the incorrect number of documents!", 1, documentRepo.count());
        assertNotNull("The default document was not created!", documentRepo.findByProjectNameAndName("default", "default"));
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
