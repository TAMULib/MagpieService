package edu.tamu.app.model.impl;

import java.util.List;

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
	public void saveMetadataField() {
		Assert.assertEquals("MetadataField repository is not empty.", metadataRepo.findAll().size(), 0);
		metadataRepo.save(testMetadataField);
		Assert.assertEquals("MetadataField repository does not have saved metadata field.", metadataRepo.findAll().size(), 1);
	}
	
	@Test
	public void findMetadataField() {
		List<MetadataFieldImpl> assertMetadataFields =  metadataRepo.getMetadataFieldsByName("testDocument.txt");
		Assert.assertEquals("Test metadata field was not added.", testMetadataField.getName(), assertMetadataFields.get(0).getName());
	}
	
	@Test
	public void deleteMetadataField() {
		metadataRepo.deleteByName(testMetadataField.getName());
		Assert.assertEquals("Test metadata field was not removed.", metadataRepo.findAll().size(), 0);
	}
	
	@After
	public void cleanUp() {
		
	}
	
}
