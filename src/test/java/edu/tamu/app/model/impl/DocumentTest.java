package edu.tamu.app.model.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
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
import edu.tamu.app.model.repo.DocumentRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class DocumentTest {
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testMethod() {
		
		DocumentImpl testDocument1 = new DocumentImpl("testFile1", "Unassigned");
		
		DocumentImpl testDocument2 = new DocumentImpl("testFile1", "Unassigned");
		
		documentRepo.save(testDocument1);
		DocumentImpl assertDocument = documentRepo.findByFilename("testFile1");
		Assert.assertEquals("Test Document 1 was not added.", testDocument1.getFilename(), assertDocument.getFilename());
		
		documentRepo.save(testDocument2);		
		List<DocumentImpl> allDocuments = (List<DocumentImpl>) documentRepo.findAll();
		Assert.assertEquals("Duplicate filename found.", 1, allDocuments.size());
		
		documentRepo.delete(testDocument1);
		allDocuments = (List<DocumentImpl>) documentRepo.findAll();
		Assert.assertEquals("Test Document 1 was not removed.", 0, allDocuments.size());
		
	}
}
