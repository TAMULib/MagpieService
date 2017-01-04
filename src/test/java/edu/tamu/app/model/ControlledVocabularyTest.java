package edu.tamu.app.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.annotations.Order;
import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.runner.OrderedRunner;

@WebAppConfiguration
@ActiveProfiles({"test"})
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class ControlledVocabularyTest {
	
	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
	
	@Before
	public void setUp() {
	    Assert.assertEquals("ControlledVocabularyRepo is not empty.", 0, controlledVocabularyRepo.count());
	}
	
	@Test
	@Order(1)
	public void testCreateControlledVocabulary() {
		ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
		Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		Assert.assertEquals("Expected test ControlledVocabulary was not created.", "test", testControlledVocabulary.getValue());
	}
	
	@Test
	@Order(2)
	public void testDuplicateControlledVocabulary() {
		controlledVocabularyRepo.create("test");
		controlledVocabularyRepo.create("test");
		Assert.assertEquals("Test ControlledVocabulary duplicate.", 1, controlledVocabularyRepo.count());
	}
	
	@Test
	@Order(3)
	public void testFindControlledVocabulary() {
		ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
		Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		ControlledVocabulary assertControlledVocabulary = controlledVocabularyRepo.findByValue("test");
		Assert.assertEquals("Expected test ControlledVocabulary was not created.", testControlledVocabulary.getValue(), assertControlledVocabulary.getValue());
	}
	
	@Test
	@Order(4)
	public void testDeleteControlledVocabulary() {
		ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
		System.out.println(testControlledVocabulary);
		Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		controlledVocabularyRepo.delete(testControlledVocabulary);
		Assert.assertEquals("Test ControlledVocabulary was not deleted.", 0, controlledVocabularyRepo.count());
	}
	
	@After
	public void cleanUp() {
		controlledVocabularyRepo.deleteAll();
	}
	
}
