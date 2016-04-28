package edu.tamu.app.model.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.config.TestDataSourceConfiguration;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
@ContextConfiguration(classes = { TestDataSourceConfiguration.class })
public class ProjectFieldProfileTest {
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectFieldProfileRepo;
		
	private Project testProject;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {
		testProject = projectRepo.create("testProject");
	}
	
	@Test
	public void testSaveProjectFieldProfile() {
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		ProjectLabelProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
		Assert.assertEquals("Test ProjectFieldProfile with expected project was not created.", "testProject", testProfile.getProject().getName());
	}
	
	@Test
	public void testDuplicateProjectFieldProfile() {
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile duplicate was created.", 1, projectFieldProfileRepo.count());
	}
	
	@Test
	public void testFindProjectFieldProfile() {
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		ProjectLabelProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
		ProjectLabelProfile assertProfile = projectFieldProfileRepo.findByProjectAndGlossAndRepeatableAndReadOnlyAndHiddenAndRequiredAndInputTypeAndDefaultValue(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile with expected project was not found.", testProfile.getProject().getName(), assertProfile.getProject().getName());
	}
	
	@Test
	public void testDeleteProjectFieldProfile() {
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		ProjectLabelProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
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
