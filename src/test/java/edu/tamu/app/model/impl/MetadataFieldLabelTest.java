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
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class MetadataFieldLabelTest {
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@BeforeClass
    public static void init()  throws InterruptedException {
		Thread.sleep(5000);
    }
	
	@Before
	public void setUp() {
		metadataFieldLabelRepo.deleteAll();
	}
	
	@Test
	public void testCreateMetadataFieldLabel() {
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.create("test");
		Assert.assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
	}
	
	@Test
	public void testDuplicateMetadataFieldLabel() {
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.create("test");
		Assert.assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.create("test");
		Assert.assertEquals("MetadataFieldLabel has duplicate.", 1, metadataFieldLabelRepo.count());
	}
	
	@Test
	public void testFindMetadataFieldLabel() {
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		MetadataFieldLabel assertLabel = metadataFieldLabelRepo.create("test");
		Assert.assertEquals("MetadataFieldLabel was not found.", assertLabel.getName(), metadataFieldLabelRepo.findByName("test").getName());
	}
	
	@Test
	public void testDeleteMetadataFieldLabel() {
		Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.create("test");
		Assert.assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
		metadataFieldLabelRepo.delete(metadataFieldLabelRepo.findByName("test"));		
		Assert.assertEquals("MetadataFieldLabel was not deleted.", 0, metadataFieldLabelRepo.count());
	}
	
	@After
	public void cleanUp() {
		metadataFieldLabelRepo.deleteAll();
	}
	
}
