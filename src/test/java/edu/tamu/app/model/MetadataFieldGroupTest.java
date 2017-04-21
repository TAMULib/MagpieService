package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.app.annotations.Order;
import edu.tamu.app.enums.InputType;

public class MetadataFieldGroupTest extends AbstractModelTest {

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject");
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        testDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "documentPath", "Unassigned");
        metadataFieldLabelRepo.save(testLabel);
        assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldGroupRepo.count());
    }

    @Test
    @Order(1)
    public void testCreateMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());
        assertEquals("Expected Test MetadataField was not created.", testLabel.getName(), testFieldGroup.getLabel().getName());
    }

    @Test
    @Order(2)
    public void testFindMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());
        testFieldGroup = metadataFieldGroupRepo.findByDocumentAndLabel(testDocument, testLabel);
        assertEquals("Test MetadataField was not found.", testLabel.getName(), testFieldGroup.getLabel().getName());
    }

    @Test
    @Order(3)
    public void testDeleteMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("Document repository is empty.", 1, metadataFieldGroupRepo.count());
        metadataFieldGroupRepo.delete(testFieldGroup);
        assertEquals("Test Document was not removed.", 0, metadataFieldGroupRepo.count());
    }

    @Test
    @Order(4)
    public void testCascadeOnDeleteMetadataField() {

        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("Test field was not created.", 1, metadataFieldGroupRepo.count());

        assertEquals("MetadataFieldValue repository is not empty.", 0, metadataFieldValueRepo.count());
        MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testFieldGroup);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());

        testFieldGroup.addValue(testValue);

        testFieldGroup = metadataFieldGroupRepo.save(testFieldGroup);

        assertEquals("Test MetadataField with expected MetadataFieldValue was not save.", testValue.getValue(), testFieldGroup.getValues().get(0).getValue());

        metadataFieldGroupRepo.delete(testFieldGroup);

        assertEquals("Test field was not deleted.", 0, metadataFieldGroupRepo.count());

        assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
    }

}
