package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

public class MetadataFieldGroupTest extends AbstractModelTest {

    @BeforeEach
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        testDocument = documentRepo.create(testProject, "testDocument", "documentPath", "Unassigned");
        metadataFieldLabelRepo.save(testLabel);
        assertEquals(0, metadataFieldGroupRepo.count(), "MetadataFieldRepo is not empty.");
    }

    @Test
    public void testCreateMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals(1, metadataFieldGroupRepo.count(), "Test MetadataField was not created.");
        assertEquals(testLabel.getName(), testFieldGroup.getLabel().getName(), "Expected Test MetadataField was not created.");
    }

    @Test
    public void testDuplicateDocument() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            metadataFieldGroupRepo.create(testDocument, testLabel);
            metadataFieldGroupRepo.create(testDocument, testLabel);
        });
    }

    @Test
    public void testFindMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals(1, metadataFieldGroupRepo.count(), "Test MetadataField was not created.");
        testFieldGroup = metadataFieldGroupRepo.findByDocumentAndLabel(testDocument, testLabel);
        assertEquals(testLabel.getName(), testFieldGroup.getLabel().getName(), "Test MetadataField was not found.");
    }

    @Test
    @Transactional
    public void testDeleteMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals(1, metadataFieldGroupRepo.count(), "Document repository is empty.");
        metadataFieldGroupRepo.delete(testFieldGroup);
        assertEquals(0, metadataFieldGroupRepo.count(), "Test Document was not removed.");
    }

    @Test
    @Transactional
    public void testCascadeOnDeleteMetadataField() {

        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals(1, metadataFieldGroupRepo.count(), "Test field was not created.");

        assertEquals(0, metadataFieldValueRepo.count(), "MetadataFieldValue repository is not empty.");
        MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testFieldGroup);
        assertEquals(1, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not created.");

        testFieldGroup.addValue(testValue);

        testFieldGroup = metadataFieldGroupRepo.save(testFieldGroup);

        assertEquals(testValue.getValue(), testFieldGroup.getValues().get(0).getValue(), "Test MetadataField with expected MetadataFieldValue was not save.");

        metadataFieldGroupRepo.delete(testFieldGroup);

        assertEquals(0, metadataFieldGroupRepo.count(), "Test field was not deleted.");

        assertEquals(0, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not deleted.");
    }

}
