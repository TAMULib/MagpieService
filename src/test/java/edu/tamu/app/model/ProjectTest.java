package edu.tamu.app.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.annotations.Order;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.runner.OrderedRunner;

@WebAppConfiguration
@ActiveProfiles({ "test" })
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class ProjectTest {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Before
    public void setUp() {
        Assert.assertEquals("ProjectRepo is not empty.", 0, projectRepo.count());
    }

    @Test
    @Order(1)
    public void testSaveProject() {
        Project assertProject = projectRepo.create("testProject");
        Assert.assertEquals("Test Project was not created.", 1, projectRepo.count());
        Assert.assertEquals("Expected Test Project was not created.", "testProject", assertProject.getName());
    }

    @Test
    @Order(2)
    public void testDuplicateProject() {
        projectRepo.create("testProject");
        projectRepo.create("testProject");
        Assert.assertEquals("Duplicate Test Project was created.", 1, projectRepo.count());
    }

    @Test
    @Order(3)
    public void testFindProject() {
        projectRepo.create("testProject");
        Assert.assertEquals("Test Project was not created.", 1, projectRepo.count());
        Project assertProject = projectRepo.findByName("testProject");
        Assert.assertEquals("Test Project was not found.", "testProject", assertProject.getName());
    }

    @Test
    @Order(4)
    public void testDeleteProject() {
        Project assertProject = projectRepo.create("testProject");
        Assert.assertEquals("Test Project was not created.", 1, projectRepo.count());
        projectRepo.delete(assertProject);
        Assert.assertEquals("Test Project was not deleted.", 0, projectRepo.count());
    }

    @Test
    @Order(5)
    public void testCascadeOnDeleteProject() {
        Project testProject = projectRepo.create("testProject");
        Assert.assertEquals("Test Project was not created.", 1, projectRepo.count());

        Assert.assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
        Document testDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "documentPath", "Unassigned");
        Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());

        testProject.addDocument(testDocument);

        testProject = projectRepo.save(testProject);

        Assert.assertEquals("Test Project does not have any documents.", 1, testProject.getDocuments().size());

        projectRepo.delete(testProject);

        Assert.assertEquals("Test Document was not deleted.", 0, documentRepo.count());
    }

    @After
    public void cleanUp() {
        projectRepo.deleteAll();
        documentRepo.deleteAll();
    }

}
