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
import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDataSourceConfiguration.class})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    					  DirtiesContextTestExecutionListener.class,
    					  TransactionalTestExecutionListener.class,
    					  DbUnitTestExecutionListener.class })
public class ControlledVocabularyTest {
	
	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
	
	
	@Before
	public void setUp() {
		controlledVocabularyRepo.deleteAll();
	}
	
	@Test
	public void testCreateControlledVocabulary() {
		Assert.assertEquals("ControlledVocabularyRepo is not empty.", 0, controlledVocabularyRepo.count());
		ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
		Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		Assert.assertEquals("Expected test ControlledVocabulary was not created.", "test", testControlledVocabulary.getValue());
	}
	
	@Test
	public void testDuplicateControlledVocabulary() {
		Assert.assertEquals("ControlledVocabularyRepo is not empty.", 0, controlledVocabularyRepo.count());
		controlledVocabularyRepo.create("test");
		controlledVocabularyRepo.create("test");
		Assert.assertEquals("Test ControlledVocabulary was created duplicate.", 1, controlledVocabularyRepo.count());
		
	}
	
	@Test
	public void testFindControlledVocabulary() {
		Assert.assertEquals("ControlledVocabularyRepo is not empty.", 0, controlledVocabularyRepo.count());
		ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
		Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		ControlledVocabulary assertControlledVocabulary = controlledVocabularyRepo.findByValue("test");
		Assert.assertEquals("Expected test ControlledVocabulary was not created.", testControlledVocabulary.getValue(), assertControlledVocabulary.getValue());
	}
	
	@Test
	public void testDeleteControlledVocabulary() {
		Assert.assertEquals("ControlledVocabularyRepo is not empty.", 0, controlledVocabularyRepo.count());
		ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
		Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		controlledVocabularyRepo.delete(testControlledVocabulary);
		Assert.assertEquals("Test ControlledVocabulary was not deleted.", 0, controlledVocabularyRepo.count());
		
	}
	
	@After
	public void cleanUp() {
		controlledVocabularyRepo.deleteAll();
	}
	
}
