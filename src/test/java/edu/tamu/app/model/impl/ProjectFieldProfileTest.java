package edu.tamu.app.model.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import edu.tamu.app.config.TestDataSourceConfiguration;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectFieldProfile;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectFieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class ProjectFieldProfileTest {
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private ProjectFieldProfileRepo projectFieldProfileRepo;
	
	private Project testProject;
	
	private MetadataFieldLabel testLabel;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {		
		testProject = projectRepo.create("testProject");
		testLabel = metadataFieldLabelRepo.create("testLabel");
	}
	
	@Test
	public void testSaveProjectFieldProfile() {
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		ProjectFieldProfile testProfile = projectFieldProfileRepo.create(testLabel, testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
		Assert.assertEquals("Test ProjectFieldProfile with expected label was not created.", "testLabel", testProfile.getLabel().getName());
		Assert.assertEquals("Test ProjectFieldProfile with expected project was not created.", "testProject", testProfile.getProject().getName());
	}
	
	@Test
	public void testDuplicateProjectFieldProfile() {
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		projectFieldProfileRepo.create(testLabel, testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		projectFieldProfileRepo.create(testLabel, testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile duplicate was created.", 1, projectFieldProfileRepo.count());
	}
	
	@Test
	public void testFindProjectFieldProfile() {
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		ProjectFieldProfile testProfile = projectFieldProfileRepo.create(testLabel, testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
		ProjectFieldProfile assertProfile = projectFieldProfileRepo.findByLabelAndProject(testLabel, testProject);
		Assert.assertEquals("Test ProjectFieldProfile with expected label was not found.", testProfile.getLabel().getName(), assertProfile.getLabel().getName());
		Assert.assertEquals("Test ProjectFieldProfile with expected project was not found.", testProfile.getProject().getName(), assertProfile.getProject().getName());
	}
	
	@Test
	public void testDeleteProjectFieldProfile() {
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		ProjectFieldProfile testProfile = projectFieldProfileRepo.create(testLabel, testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
		projectFieldProfileRepo.delete(testProfile);
		Assert.assertEquals("Test ProjectFieldProfile was not deleted.", 0, projectFieldProfileRepo.count());
	}
	
	@After
	public void cleanUp() {
		projectRepo.deleteAll();
		metadataFieldLabelRepo.deleteAll();
		projectFieldProfileRepo.deleteAll();
	}
	
}
