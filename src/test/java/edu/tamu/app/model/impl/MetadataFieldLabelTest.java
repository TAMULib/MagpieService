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
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class MetadataFieldLabelTest {
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectFieldProfileRepo;
	
	private Project testProject;
	
	private ProjectLabelProfile testProfile;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {
		testProject = projectRepo.create("testProject");
		testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
	}
	
	@Test
	public void testCreateMetadataFieldLabel() {
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.create("test", testProfile);
		Assert.assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
	}
	
	@Test
	public void testDuplicateMetadataFieldLabel() {
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.create("test", testProfile);
		metadataFieldLabelRepo.create("test", testProfile);
		Assert.assertEquals("MetadataFieldLabel has duplicate.", 1, metadataFieldLabelRepo.count());
	}
	
	@Test
	public void testFindMetadataFieldLabel() {
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		MetadataFieldLabel assertLabel = metadataFieldLabelRepo.create("test", testProfile);
		Assert.assertEquals("MetadataFieldLabel was not found.", assertLabel.getName(), metadataFieldLabelRepo.findByName("test").getName());
	}
	
	@Test
	public void testDeleteMetadataFieldLabel() {
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.create("test", testProfile);
		Assert.assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.delete(metadataFieldLabelRepo.findByName("test"));		
		Assert.assertEquals("MetadataFieldLabel was not deleted.", 0, metadataFieldLabelRepo.count());
	}
	
	@After
	public void cleanUp() {
		metadataFieldLabelRepo.deleteAll();
		projectFieldProfileRepo.deleteAll();
		projectRepo.deleteAll();
	}
	
}
