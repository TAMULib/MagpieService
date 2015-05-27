package edu.tamu.app.model.impl;

import org.junit.After;
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
	
	private DocumentImpl testDocument = new DocumentImpl("testFile", "project", "Unassigned", null, null, null);
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void saveDocument() {
		Assert.assertEquals("Document repository is not empty.", documentRepo.findAll().size(), 0);
		documentRepo.save(testDocument);
		Assert.assertEquals("Document repository does not have saved document.", documentRepo.findAll().size(), 1);
	}
	
	@Test
	public void findDocument() {
		DocumentImpl assertDocument = documentRepo.findByName("testFile");
		Assert.assertEquals("Test Document was not added.", testDocument.getName(), assertDocument.getName());
	}
	
	@Test
	public void deleteDocument() {
		documentRepo.delete(testDocument);
		Assert.assertEquals("Test Document was not removed.", documentRepo.findAll().size(), 0);
	}
	
	@After
	public void cleanUp() {
		
	}
	
}
