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
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
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
	private MetadataFieldGroupRepo metadataFieldRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectFieldProfileRepo;
	
	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
		
	private Project testProject;
	
	private Document testDocument;
	
	private MetadataFieldLabel testLabel;
	
	private ProjectLabelProfile testProfile;
	
	private MetadataFieldGroup testField;
	
	@BeforeClass
    public static void init() {
		
    }
	
	@Before
	public void setUp() {
		testProject = projectRepo.create("testProject");
		testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
		testDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");		
		testField = metadataFieldRepo.create(testDocument, testLabel);	
	}
	
	@Test
	public void testSaveMetadataFieldValue() {
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		Assert.assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());	
	}
	
	@Test
	public void testSaveWithControlCharacterMetadataFieldValue() {
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.create("test\n\r\t\b\f", testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		Assert.assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());	
	}
	
	@Test
	public void testFindMetadataFieldValue() {
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		testValue = metadataFieldValueRepo.findByValueAndField("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not found.", "test", testValue.getValue());
	}
	
	@Test
	public void testDeleteMetadataFieldValue() {
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		metadataFieldValueRepo.delete(testValue);
		Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
	}
	
	@Test
	public void testCascadeOnDeleteMetadataFieldValue() {
		
		Assert.assertEquals("ControlledVocabularyRepo is not empty.", 0, controlledVocabularyRepo.count());
		ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
		Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		
		Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.create(testControlledVocabulary, testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		
		Assert.assertEquals("Test MetadataFieldValue with ControlledVocabulary was not created.", testControlledVocabulary.getValue(), testValue.getValue());
				
		metadataFieldValueRepo.delete(testValue);
		
		Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
				
		Assert.assertEquals("Test ControlledVocabulary was deleted.", 1, controlledVocabularyRepo.count());
	}
	
	@After
	public void cleanUp() {
		controlledVocabularyRepo.deleteAll();
		projectFieldProfileRepo.deleteAll();
		metadataFieldValueRepo.deleteAll();
		metadataFieldLabelRepo.deleteAll();
		metadataFieldRepo.deleteAll();	
		documentRepo.deleteAll();
		projectRepo.deleteAll();
	}
	
}