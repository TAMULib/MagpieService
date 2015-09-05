package edu.tamu.app.model.impl;

import java.util.ArrayList;

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
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class DocumentTest {

	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
		
	private Document mockDocument = new Document("testDocument", null, null, null, null, "Unassigned", new ArrayList<MetadataField>());
		
	@BeforeClass
    public static void init() {
		
    }
	 
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testCreateDocument() {
		Assert.assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
		Document testDocument = documentRepo.create(mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus(), mockDocument.getMetadataFields());
		Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());
		Assert.assertEquals("Expected Test Document was not created.", mockDocument.getName(), testDocument.getName());
	}
	
	@Test
	public void testFindDocument() {	
		Assert.assertEquals("Test Document already exists.", null, documentRepo.findByName("testFile"));
		documentRepo.create(mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus(), mockDocument.getMetadataFields());
		Document testDocument = documentRepo.findByName(mockDocument.getName());
		Assert.assertEquals("Test Document was not found.", mockDocument.getName(), testDocument.getName());
	}
	
	@Test
	public void testDeleteDocument() {
		Document testDocument = documentRepo.create(mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus(), mockDocument.getMetadataFields());
		Assert.assertEquals("DocumentRepo is empty.", 1, documentRepo.count());
		documentRepo.delete(testDocument);
		Assert.assertEquals("Test Document was not removed.", 0, documentRepo.count());
	}
	
	@Test
	public void testCascadeOnDeleteDocument() {
		Assert.assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
		Document testDocument = documentRepo.create(mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus(), mockDocument.getMetadataFields());
		Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());
		
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		MetadataFieldLabel testLabel = metadataFieldLabelRepo.create("testLabel");
		Assert.assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
		
		Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldRepo.count());
		MetadataField testField = metadataFieldRepo.create(testDocument, testLabel);
		Assert.assertEquals("Test MetadataField was not created.", 1, metadataFieldRepo.count());
		
		Assert.assertEquals("MetadataFieldValue repository is not empty.", 0, metadataFieldValueRepo.count());
		metadataFieldValueRepo.create("test", testField);
		Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
		
		documentRepo.delete(testDocument);
		
		Assert.assertEquals("Test Document was not deleted.", 0, documentRepo.count());
	
		Assert.assertEquals("MetadataFieldLabel was deleted.", 1, metadataFieldLabelRepo.count());
		
		Assert.assertEquals("Test MetadataField was not deleted.", 0, metadataFieldRepo.count());
		
		Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());		
	}
	
	@After
	public void cleanUp() {
		documentRepo.deleteAll();
		metadataFieldRepo.deleteAll();
		metadataFieldLabelRepo.deleteAll();
		metadataFieldValueRepo.deleteAll();
	}
	
}
