package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.app.annotations.Order;

public class ControlledVocabularyTest extends AbstractClass {

	@Test
	@Order(1)
	public void testCreateControlledVocabulary() {
		testControlledVocabulary = controlledVocabularyRepo.create("test");
		assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		assertEquals("Expected test ControlledVocabulary was not created.", "test", testControlledVocabulary.getValue());
	}
	
	@Test
	@Order(2)
	public void testDuplicateControlledVocabulary() {
		controlledVocabularyRepo.create("test");
		controlledVocabularyRepo.create("test");
		assertEquals("Test ControlledVocabulary duplicate.", 1, controlledVocabularyRepo.count());
	}
	
	@Test
	@Order(3)
	public void testFindControlledVocabulary() {
		testControlledVocabulary = controlledVocabularyRepo.create("test");
		assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		ControlledVocabulary assertControlledVocabulary = controlledVocabularyRepo.findByValue("test");
		assertEquals("Expected test ControlledVocabulary was not created.", testControlledVocabulary.getValue(), assertControlledVocabulary.getValue());
	}
	
	@Test
	@Order(4)
	public void testDeleteControlledVocabulary() {
		testControlledVocabulary = controlledVocabularyRepo.create("test");
		assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());
		controlledVocabularyRepo.delete(testControlledVocabulary);
		assertEquals("Test ControlledVocabulary was not deleted.", 0, controlledVocabularyRepo.count());
	}

}
