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
import edu.tamu.app.model.repo.MetadataFieldRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class MetadataTest {
	
	@Autowired
	private MetadataFieldRepo metadataRepo;
	
	private MetadataFieldImpl testMetadataField = new MetadataFieldImpl("testDocument.txt", "testMetadataField");
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testSaveMetadataField() {
		Assert.assertEquals("MetadataField repository is not empty.", 0, metadataRepo.count());
		metadataRepo.save(testMetadataField);
		Assert.assertEquals("Metadata field was not saved.", 1, metadataRepo.count());
	}
	
	@Test
	public void testFindMetadataField() {
		Assert.assertEquals("MetadataField repository is not empty.", 0, metadataRepo.count());
		metadataRepo.save(testMetadataField);
		Assert.assertEquals("MetadataField repository is empty.", 1, metadataRepo.count());
		Assert.assertEquals("Test metadata field was not found.", metadataRepo.getMetadataFieldsByName("testDocument.txt").get(0).getName(), testMetadataField.getName());
	}
	
	@Test
	public void testDeleteMetadataField() {
		Assert.assertEquals("MetadataField repository is not empty.", 0, metadataRepo.count());
		metadataRepo.save(testMetadataField);
		Assert.assertEquals("MetadataField repository is empty.", 1, metadataRepo.count());
		metadataRepo.deleteByName(testMetadataField.getName());
		Assert.assertEquals("Test metadata field was not removed.", 0, metadataRepo.count());
	}
	
	@After
	public void cleanUp() {
		metadataRepo.deleteAll();
	}
	
}
