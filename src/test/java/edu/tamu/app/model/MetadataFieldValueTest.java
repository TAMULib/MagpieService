package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

public class MetadataFieldValueTest extends AbstractModelTest {

    @BeforeEach
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        testDocument = documentRepo.create(testProject, "testDocument", "documentPath", "Unassigned");
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals(0, metadataFieldValueRepo.count(), "MetadataFieldValueRepo is not empty.");
    }

    @Test
    public void testSaveMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testFieldGroup);
        assertEquals(1, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not created.");
        assertEquals("test", testValue.getValue(), "Expected Test MetadataFieldValue was not created.");
    }

    @Test
    public void testSaveWithControlCharacterMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test\n\r\t\b\f", testFieldGroup);
        assertEquals(1, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not created.");
        assertEquals("test", testValue.getValue(), "Expected Test MetadataFieldValue was not created.");
    }

    @Test
    public void testFindMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testFieldGroup);
        assertEquals(1, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not created.");
        testValue = metadataFieldValueRepo.findByValueAndField("test", testFieldGroup);
        assertEquals("test", testValue.getValue(), "Test MetadataFieldValue was not found.");
    }

    @Test
    @Transactional
    public void testDeleteMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testFieldGroup);
        assertEquals(1, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not created.");
        metadataFieldValueRepo.delete(testValue);
        assertEquals(0, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not deleted.");
    }

    @Test
    @Transactional
    public void testCascadeOnDeleteMetadataFieldValue() {

        testControlledVocabulary = controlledVocabularyRepo.create("test");

        assertEquals(1, controlledVocabularyRepo.count(), "Test ControlledVocabulary was not created.");

        assertEquals(0, metadataFieldValueRepo.count(), "MetadataFieldValueRepo is not empty.");
        testValue = metadataFieldValueRepo.create(testControlledVocabulary, testFieldGroup);
        assertEquals(1, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not created.");

        assertEquals(testControlledVocabulary.getValue(), testValue.getValue(), "Test MetadataFieldValue with ControlledVocabulary was not created.");

        metadataFieldValueRepo.delete(testValue);

        assertEquals(0, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not deleted.");

        assertEquals(1, metadataFieldGroupRepo.count(), "Test MetadataFieldGroup was deleted.");

        assertEquals(1, controlledVocabularyRepo.count(), "Test ControlledVocabulary was deleted.");
    }

}
