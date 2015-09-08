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
import edu.tamu.app.model.Document;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectFieldProfile;
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
public class MetadataFieldTest {
	
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
	
	private Project testProject;
	
	private Document testDocument;
	
	private MetadataFieldLabel testLabel;
	
	private ProjectFieldProfile testProfile;
		
	@BeforeClass
    public static void init() {
		
    }
		
	@Before
	public void setUp() {
		testProject = projectRepo.create("testProject");
		testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		testDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");
		testLabel = metadataFieldLabelRepo.create("testLabel");
		testLabel.addProfile(testProfile);
		metadataFieldLabelRepo.save(testLabel);
	}
	
	@Test
	public void testCreateMetadataField() {
		Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldRepo.count());
		MetadataField testField = metadataFieldRepo.create(testDocument, testLabel);
		Assert.assertEquals("Test MetadataField was not created.", 1, metadataFieldRepo.count());
		Assert.assertEquals("Expected Test MetadataField was not created.", testLabel.getName(), testField.getLabel().getName());
	}
		
	@Test
	public void testFindMetadataField() {
		Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldRepo.count());
		MetadataField testField = metadataFieldRepo.create(testDocument, testLabel);
		Assert.assertEquals("Test MetadataField was not created.", 1, metadataFieldRepo.count());
		testField = metadataFieldRepo.findByDocumentAndLabel(testDocument, testLabel);
		Assert.assertEquals("Test MetadataField was not found.", testLabel.getName(), testField.getLabel().getName());
	}
	
	@Test
	public void testDeleteMetadataField() {
		Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldRepo.count());
		MetadataField testField = metadataFieldRepo.create(testDocument, testLabel);
		Assert.assertEquals("Document repository is empty.", 1, metadataFieldRepo.count());
		metadataFieldRepo.delete(testField);
		Assert.assertEquals("Test Document was not removed.", 0, metadataFieldRepo.count());
	}
	
	@Test
	public void testCascadeOnDeleteMetadataField() {
		
		Assert.assertEquals("Field repository is not empty.", 0, metadataFieldRepo.count());
		MetadataField testField = metadataFieldRepo.create(testDocument, testLabel);
		Assert.assertEquals("Test field was not created.", 1, metadataFieldRepo.count());
		
		Assert.assertEquals("MetadataFieldValue repository is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		
		testField.addValue(testValue);
		
		testField = metadataFieldRepo.save(testField);
		
		Assert.assertEquals("Test MetadataField with expected MetadataFieldValue was not save.", testValue.getValue(), testField.getValues().get(0).getValue());
		
		metadataFieldRepo.delete(testField);
		Assert.assertEquals("Test field was not deleted.", 0, metadataFieldRepo.count());
		
		Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
	}
	
	@After
	public void cleanUp() {
		projectFieldProfileRepo.deleteAll();
		metadataFieldValueRepo.deleteAll();
		metadataFieldLabelRepo.deleteAll();
		metadataFieldRepo.deleteAll();
		documentRepo.deleteAll();
		projectRepo.deleteAll();
	}
	
}
