package edu.tamu.app.model.impl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
	public void testSaveDocument() {
		Assert.assertEquals("Document repository is not empty.", 0, documentRepo.count());
		documentRepo.save(testDocument);
		Assert.assertEquals("Test document was not saved.", 1, documentRepo.count());
	}
	
	@Test
	public void testFindDocument() {
		Assert.assertEquals("Document repository is not empty.", 0, documentRepo.count());
		documentRepo.save(testDocument);
		Assert.assertEquals("Document repository is empty.", 1, documentRepo.count());
		Assert.assertEquals("Test Document was not found.", documentRepo.findByName("testFile").getName(), testDocument.getName());
	}
	
	@Test
	public void testDeleteDocument() {
		Assert.assertEquals("Document repository is not empty.", 0, documentRepo.count());
		documentRepo.save(testDocument);
		Assert.assertEquals("Document repository is empty.", 1, documentRepo.count());
		documentRepo.delete(testDocument);
		Assert.assertEquals("Test Document was not removed.", 0, documentRepo.count());
	}
	
	@After
	public void cleanUp() {
		documentRepo.deleteAll();
	}
	
}
