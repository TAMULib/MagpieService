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
import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.runner.OrderedRunner;

@WebAppConfiguration
@ActiveProfiles({ "test" })
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class ProjectFieldProfileTest {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private FieldProfileRepo projectFieldProfileRepo;

    private Project testProject;

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject");
        Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
    }

    @Test
    @Order(1)
    public void testSaveProjectFieldProfile() {
        FieldProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
        Assert.assertEquals("Test ProjectFieldProfile with expected project was not created.", "testProject", testProfile.getProject().getName());
    }

    @Test
    @Order(2)
    public void testDuplicateProjectFieldProfile() {
        projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        Assert.assertEquals("Test ProjectFieldProfile duplicate was created.", 1, projectFieldProfileRepo.count());
    }

    @Test
    @Order(3)
    public void testFindProjectFieldProfile() {
        FieldProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
        FieldProfile assertProfile = projectFieldProfileRepo.findByProjectAndGloss(testProject, "testGloss");
        Assert.assertEquals("Test ProjectFieldProfile with expected project was not found.", testProfile.getProject().getName(), assertProfile.getProject().getName());
    }

    @Test
    @Order(4)
    public void testDeleteProjectFieldProfile() {
        FieldProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
        projectFieldProfileRepo.delete(testProfile);
        Assert.assertEquals("Test ProjectFieldProfile was not deleted.", 0, projectFieldProfileRepo.count());
    }

    @After
    public void cleanUp() {
        projectFieldProfileRepo.deleteAll();
        projectRepo.deleteAll();
    }

}
