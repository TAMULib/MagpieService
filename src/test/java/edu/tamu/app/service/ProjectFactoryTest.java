package edu.tamu.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@ActiveProfiles("test")
@SpringBootTest(classes = WebServerInit.class)
public class ProjectFactoryTest {

    @Autowired
    private ProjectFactory projectFactory;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Test
    public void testReadProjectNode() {
        JsonNode projectsNode = projectFactory.readProjectsNode();
        assertNotNull(projectsNode, "The projects node was not read!");
    }

    @Test
    public void testGetProject() {
        Project project = projectFactory.getOrCreateProject("default");
        assertNotNull(project, "The project was not created!");
        assertEquals(1, projectRepo.count(), "The project repo has the incorrect number of projects!");
        assertEquals(0, project.getRepositories().size(), "The project has the incorrect number of repositories!");
        assertEquals(0, project.getAuthorities().size(), "The project has the incorrect number of authorities!");
        assertEquals(0, project.getSuggestors().size(), "The project has the incorrect number of suggestors!");
    }

    @Test
    public void testGetProjectNode() {
        JsonNode profileNode = projectFactory.getProjectNode("default");
        assertNotNull(profileNode, "The profile node was not retrieved!");
    }

    @Test
    public void testGetProjectFields() {
        List<MetadataFieldGroup> projectFields = projectFactory.getProjectFields("default");
        assertEquals(projectFields.size(), 2, "The project fields did not have the correct number of MetadataFieldFroups!");
    }

    @AfterEach
    public void cleanUp() {
        documentRepo.deleteAll();
        projectRepo.deleteAll();
        fieldProfileRepo.deleteAll();
    }
}
