package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
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
        assertNotNull("The projects node was not read!", projectsNode);
    }

    @Test
    public void testGetProject() {
        Project project = projectFactory.getOrCreateProject("default");
        assertNotNull("The project was not created!", project);
        assertEquals("The project repo has the incorrect number of projects!", 1, projectRepo.count());
        assertEquals("The project has the incorrect number of repositories!", 0, project.getRepositories().size());
        assertEquals("The project has the incorrect number of authorities!", 0, project.getAuthorities().size());
        assertEquals("The project has the incorrect number of suggestors!", 0, project.getSuggestors().size());
    }

    @Test
    public void testGetProjectNode() {
        JsonNode profileNode = projectFactory.getProjectNode("default");
        assertNotNull("The profile node was not retrieved!", profileNode);
    }

    @Test
    public void testGetProjectFields() {
        List<MetadataFieldGroup> projectFields = projectFactory.getProjectFields("default");
        assertEquals("The project fields did not have the correct number of MetadataFieldFroups!", projectFields.size(), 2);
    }

    @After
    public void cleanUp() {
        fieldProfileRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
    }
}
