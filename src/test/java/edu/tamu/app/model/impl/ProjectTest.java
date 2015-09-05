package edu.tamu.app.model.impl;

import java.util.ArrayList;

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
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class ProjectTest {
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testSaveProject() {
		Assert.assertEquals("ProjectRepo is not empty.", 0, projectRepo.count());
		Project assertProject = projectRepo.create("testProject");
		Assert.assertEquals("Test Project was not created.", 1, projectRepo.count());
		Assert.assertEquals("Expected Test Project was not created.", "testProject", assertProject.getName());
	}
	
	@Test
	public void testDuplicateProject() {
		Assert.assertEquals("ProjectRepo is not empty.", 0, projectRepo.count());
		projectRepo.create("testProject");
		projectRepo.create("testProject");
		Assert.assertEquals("Duplicate Test Project was created.", 1, projectRepo.count());
	}
	
	@Test
	public void testFindProject() {
		Assert.assertEquals("ProjectRepo is not empty.", 0, projectRepo.count());
		projectRepo.create("testProject");
		Assert.assertEquals("Test Project was not created.", 1, projectRepo.count());
		Project assertProject = projectRepo.findByName("testProject");
		Assert.assertEquals("Test Project was not found.", "testProject", assertProject.getName());
	}
	
	@Test
	public void testDeleteProject() {
		Assert.assertEquals("ProjectRepo is not empty.", 0, projectRepo.count());
		Project assertProject = projectRepo.create("testProject");
		Assert.assertEquals("Test Project was not created.", 1, projectRepo.count());
		projectRepo.delete(assertProject);
		Assert.assertEquals("Test Project was not deleted.", 0, projectRepo.count());
	}
	
	@Test
	public void testCascadeOnDeleteProject() {
		
		// assuming documents of a project are preserved
		
		Assert.assertEquals("ProjectRepo is not empty.", 0, projectRepo.count());
		Project testProject = projectRepo.create("testProject");
		Assert.assertEquals("Test Project was not created.", 1, projectRepo.count());
		
		Assert.assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
		Document testDocument = documentRepo.create("testDocument", null, null, null, null, "Unassigned");
		Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());
		
		testProject.addDocument(testDocument);
		
		testProject = projectRepo.save(testProject);
		
		Assert.assertEquals("Test Project does not have any documents.", 1, testProject.getDocuments().size());
		
		projectRepo.delete(testProject);
		
		Assert.assertEquals("Test Document was deleted.", 1, documentRepo.count());
	}
	
	@After
	public void cleanUp() {
		projectRepo.deleteAll();
		documentRepo.deleteAll();
	}
	
}
