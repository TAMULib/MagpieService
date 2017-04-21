package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.app.annotations.Order;
import edu.tamu.app.enums.InputType;

public class MetadataFieldValueTest extends AbstractModelTest {

    @Before
    public void setUp() {
    	testProject = projectRepo.create("testProject");
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        testDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "documentPath", "Unassigned");
        testField = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
    }

    @Test
    @Order(1)
    public void testSaveMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testField);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());
    }

    @Test
    @Order(2)
    public void testSaveWithControlCharacterMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test\n\r\t\b\f", testField);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());
    }

    @Test
    @Order(3)
    public void testFindMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testField);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        testValue = metadataFieldValueRepo.findByValueAndField("test", testField);
        assertEquals("Test MetadataFieldValue was not found.", "test", testValue.getValue());
    }

    @Test
    @Order(4)
    public void testDeleteMetadataFieldValue() {
        testValue = metadataFieldValueRepo.create("test", testField);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        metadataFieldValueRepo.delete(testValue);
        assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
    }

    @Test
    @Order(5)
    public void testCascadeOnDeleteMetadataFieldValue() {
        ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
        assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());

        assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
        testValue = metadataFieldValueRepo.create(testControlledVocabulary, testField);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());

        assertEquals("Test MetadataFieldValue with ControlledVocabulary was not created.", testControlledVocabulary.getValue(), testValue.getValue());

        metadataFieldValueRepo.delete(testValue);

        assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());

        assertEquals("Test ControlledVocabulary was deleted.", 1, controlledVocabularyRepo.count());
    }

}
