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
import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectFieldProfile;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectFieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class MetadataFieldValueTest {
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Autowired
	private ProjectFieldProfileRepo projectFieldProfileRepo;
	
	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
		
	private Project testProject;
	
	private Document testDocument;
	
	private MetadataFieldLabel testLabel;
	
	private ProjectFieldProfile testProfile;
	
	private MetadataField testField;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {
		testProject = projectRepo.save(new Project("testProject"));
		testProfile = projectFieldProfileRepo.save(new ProjectFieldProfile(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default"));
		testDocument = documentRepo.save(new Document(testProject, "testDocument", null, null, null, null, "Unassigned"));
		testLabel = metadataFieldLabelRepo.save(new MetadataFieldLabel("testLabel", testProfile));
		testField = metadataFieldRepo.save(new MetadataField(testDocument, testLabel));
	}
	
	@Test
	public void testSaveMetadataFieldValue() {
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.save(new MetadataFieldValue("test", testField));
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		Assert.assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());	
	}
	
	@Test
	public void testFindMetadataFieldValue() {
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.save(new MetadataFieldValue("test", testField));
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		testValue = metadataFieldValueRepo.findByValueAndField("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not found.", "test", testValue.getValue());
	}
	
	@Test
	public void testDeleteMetadataFieldValue() {
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.save(new MetadataFieldValue("test", testField));
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		metadataFieldValueRepo.delete(testValue);
		Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
	}
	
	@Test
	public void testCascadeOnDeleteMetadataFieldValue() {
		
		Assert.assertEquals("ControlledVocabularyRepo is not empty.", 0, controlledVocabularyRepo.count());
		ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.save(new ControlledVocabulary("test"));
		Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.save(new MetadataFieldValue(testControlledVocabulary, testField));
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		
		Assert.assertEquals("Test MetadataFieldValue with ControlledVocabulary was not created.", testControlledVocabulary.getValue(), testValue.getValue());
				
		metadataFieldValueRepo.delete(testValue);
		
		Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
				
		Assert.assertEquals("Test ControlledVocabulary was deleted.", 1, controlledVocabularyRepo.count());
	}
	
	@After
	public void cleanUp() {
		projectRepo.deleteAll();
		documentRepo.deleteAll();
		metadataFieldRepo.deleteAll();		
		metadataFieldLabelRepo.deleteAll();
		metadataFieldValueRepo.deleteAll();
		projectFieldProfileRepo.deleteAll();
		controlledVocabularyRepo.deleteAll();
	}
	
}
