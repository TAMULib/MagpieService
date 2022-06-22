package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class ControlledVocabularyTest extends AbstractModelTest {

    @Test
    public void testCreateControlledVocabulary() {
        testControlledVocabulary = controlledVocabularyRepo.create("test");
        assertEquals(1, controlledVocabularyRepo.count(), "Test ControlledVocabulary was not created.");
        assertEquals("test", testControlledVocabulary.getValue(), "Expected test ControlledVocabulary was not created.");
    }

    @Test
    public void testDuplicateControlledVocabulary() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            controlledVocabularyRepo.create("test");
            controlledVocabularyRepo.create("test");
        });
    }

    @Test
    public void testFindControlledVocabulary() {
        testControlledVocabulary = controlledVocabularyRepo.create("test");
        assertEquals(1, controlledVocabularyRepo.count(), "Test ControlledVocabulary was not created.");
        ControlledVocabulary assertControlledVocabulary = controlledVocabularyRepo.findByValue("test");
        assertEquals(testControlledVocabulary.getValue(), assertControlledVocabulary.getValue(), "Expected test ControlledVocabulary was not created.");
    }

    @Test
    public void testDeleteControlledVocabulary() {
        testControlledVocabulary = controlledVocabularyRepo.create("test");
        assertEquals(1, controlledVocabularyRepo.count(), "Test ControlledVocabulary was not created.");
        controlledVocabularyRepo.delete(testControlledVocabulary);
        assertEquals(0, controlledVocabularyRepo.count(), "Test ControlledVocabulary was not deleted.");
    }

}
