package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

public class MetadataFieldValueTest extends AbstractModelTest {

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        testDocument = documentRepo.create(testProject, "testDocument", "documentPath", "Unassigned");
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
    }

    @Test
    public void testSaveMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testFieldGroup);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());
    }

    @Test
    public void testSaveWithControlCharacterMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test\n\r\t\b\f", testFieldGroup);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());
    }

    @Test
    public void testFindMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testFieldGroup);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        testValue = metadataFieldValueRepo.findByValueAndField("test", testFieldGroup);
        assertEquals("Test MetadataFieldValue was not found.", "test", testValue.getValue());
    }

    @Test
    @Transactional
    public void testDeleteMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testFieldGroup);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        metadataFieldValueRepo.delete(testValue);
        assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
    }

    @Test
    @Transactional
    public void testCascadeOnDeleteMetadataFieldValue() {

        testControlledVocabulary = controlledVocabularyRepo.create("test");

        assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());

        assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
        testValue = metadataFieldValueRepo.create(testControlledVocabulary, testFieldGroup);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());

        assertEquals("Test MetadataFieldValue with ControlledVocabulary was not created.", testControlledVocabulary.getValue(), testValue.getValue());

        metadataFieldValueRepo.delete(testValue);

        assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());

        assertEquals("Test MetadataFieldGroup was deleted.", 1, metadataFieldGroupRepo.count());

        assertEquals("Test ControlledVocabulary was deleted.", 1, controlledVocabularyRepo.count());
    }

}
