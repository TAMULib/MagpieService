package edu.tamu.app.model.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.Test;
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
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
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
public class DocumentTest {
	
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
	
	private Project testProject;
	
	private Document mockDocument;
		
	@BeforeClass
    public static void init() {
		
    }
	 
	@Before
	public void setUp() {
		testProject = projectRepo.create("testProject");
		mockDocument = new Document(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");
	}
	
	@Test
	public void testCreateDocument() {
		Assert.assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
		Document testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
		Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());
		Assert.assertEquals("Expected Test Document was not created.", mockDocument.getName(), testDocument.getName());
	}
	
	@Test
	public void testFindDocument() {	
		Assert.assertEquals("Test Document already exists.", null, documentRepo.findByName("testFile"));
		documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
		Document testDocument = documentRepo.findByName(mockDocument.getName());
		Assert.assertEquals("Test Document was not found.", mockDocument.getName(), testDocument.getName());
	}
	
	@Test
	public void testDeleteDocument() {
		Document testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
		Assert.assertEquals("DocumentRepo is empty.", 1, documentRepo.count());
		documentRepo.delete(testDocument);
		Assert.assertEquals("Test Document was not removed.", 0, documentRepo.count());
	}
	
	@Test
	public void testCascadeOnDeleteDocument() {
		Assert.assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
		Document testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
		Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());
		
		Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
		ProjectLabelProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
		Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
		
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		MetadataFieldLabel testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
		Assert.assertEquals("Test MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
		
		metadataFieldLabelRepo.save(testLabel);
		
		Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldRepo.count());
		MetadataFieldGroup testField = metadataFieldRepo.create(testDocument, testLabel);
		Assert.assertEquals("Test MetadataField was not created.", 1, metadataFieldRepo.count());
		
		Assert.assertEquals("MetadataFieldValue repository is not empty.", 0, metadataFieldValueRepo.count());
		MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		
		testDocument.addField(testField);
		testDocument = documentRepo.save(testDocument);
		
		documentRepo.delete(testDocument);
		
		Assert.assertEquals("Test Document was not deleted.", 0, documentRepo.count());
		
		Assert.assertEquals("Test MetadataFieldLabel was deleted.", 1, metadataFieldLabelRepo.count());
		
		Assert.assertEquals("Test ProjectFieldProfile was deleted.", 1, projectFieldProfileRepo.count());
		
		Assert.assertEquals("Test MetadataField was not deleted.", 0, metadataFieldRepo.count());
		
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
