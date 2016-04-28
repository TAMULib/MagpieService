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
import edu.tamu.app.model.Document;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
@ContextConfiguration(classes = { TestDataSourceConfiguration.class })
public class MetadataFieldTest {
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private MetadataFieldGroupRepo metadataFieldGroupRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectFieldProfileRepo;
	
	private Project testProject;
	
	private Document testDocument;
	
	private MetadataFieldLabel testLabel;
	
	private ProjectLabelProfile testProfile;
		
	@BeforeClass
    public static void init() {
		
    }
		
	@Before
	public void setUp() {
		testProject = projectRepo.create("testProject");
		testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
		testDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");
		metadataFieldLabelRepo.save(testLabel);
	}
	
	@Test
	public void testCreateMetadataField() {
		Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldGroupRepo.count());
		MetadataFieldGroup testField = metadataFieldGroupRepo.create(testDocument, testLabel);
		Assert.assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());
		Assert.assertEquals("Expected Test MetadataField was not created.", testLabel.getName(), testField.getLabel().getName());
	}
		
	@Test
	public void testFindMetadataField() {
		Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldGroupRepo.count());
		MetadataFieldGroup testField = metadataFieldGroupRepo.create(testDocument, testLabel);
		Assert.assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());
		testField = metadataFieldGroupRepo.findByDocumentAndLabel(testDocument, testLabel);
		Assert.assertEquals("Test MetadataField was not found.", testLabel.getName(), testField.getLabel().getName());
	}
	
	@Test
	public void testDeleteMetadataField() {
		Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldGroupRepo.count());
		MetadataFieldGroup testField = metadataFieldGroupRepo.create(testDocument, testLabel);
		Assert.assertEquals("Document repository is empty.", 1, metadataFieldGroupRepo.count());
		metadataFieldGroupRepo.delete(testField);
		Assert.assertEquals("Test Document was not removed.", 0, metadataFieldGroupRepo.count());
	}
	
	@Test
	public void testCascadeOnDeleteMetadataField() {
		
		Assert.assertEquals("Field repository is not empty.", 0, metadataFieldGroupRepo.count());
		MetadataFieldGroup testField = metadataFieldGroupRepo.create(testDocument, testLabel);
		Assert.assertEquals("Test field was not created.", 1, metadataFieldGroupRepo.count());
		
		Assert.assertEquals("MetadataFieldValue repository is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		
		testField.addValue(testValue);
		
		testField = metadataFieldGroupRepo.save(testField);
		
		Assert.assertEquals("Test MetadataField with expected MetadataFieldValue was not save.", testValue.getValue(), ((MetadataFieldValue)testField.getValues().toArray()[0]).getValue());
		
		metadataFieldGroupRepo.delete(testField);
		Assert.assertEquals("Test field was not deleted.", 0, metadataFieldGroupRepo.count());
		
		Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
	}
	
	@After
	public void cleanUp() {
		projectFieldProfileRepo.deleteAll();
		metadataFieldValueRepo.deleteAll();
		metadataFieldLabelRepo.deleteAll();
		metadataFieldGroupRepo.deleteAll();
		documentRepo.deleteAll();
		projectRepo.deleteAll();
	}
	
}
