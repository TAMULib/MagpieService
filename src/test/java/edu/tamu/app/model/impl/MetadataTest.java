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
	
	@Before
	public void setUp() {
	}
	
	@Test
	public void testMethod() {
		
		MetadataFieldImpl testMetadataField1 = new MetadataFieldImpl();
		testMetadataField1.setName("dissertation1.txt");
		
		metadataRepo.save(testMetadataField1);		
		List<MetadataFieldImpl> assertMetadataFields = metadataRepo.getMetadataFieldsByName("dissertation1.txt");
		Assert.assertEquals("Test User 1 was not added.", testMetadataField1.getName(), assertMetadataFields.get(0).getName());
	
		metadataRepo.delete(testMetadataField1);		
		List<MetadataFieldImpl> allMetadataFields = (List<MetadataFieldImpl>) metadataRepo.findAll();		
		Assert.assertEquals("Test MetadataField 1 was not removed.", 0, allMetadataFields.size());
		
	}
}
