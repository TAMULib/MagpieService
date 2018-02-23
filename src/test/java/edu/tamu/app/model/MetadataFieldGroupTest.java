package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

public class MetadataFieldGroupTest extends AbstractModelTest {

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        testDocument = documentRepo.create(testProject, "testDocument", "documentPath", "Unassigned");
        metadataFieldLabelRepo.save(testLabel);
        assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldGroupRepo.count());
    }

    @Test
    public void testCreateMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());
        assertEquals("Expected Test MetadataField was not created.", testLabel.getName(), testFieldGroup.getLabel().getName());
    }

    @Test
    public void testFindMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());
        testFieldGroup = metadataFieldGroupRepo.findByDocumentAndLabel(testDocument, testLabel);
        assertEquals("Test MetadataField was not found.", testLabel.getName(), testFieldGroup.getLabel().getName());
    }

    @Test
    @Transactional
    public void testDeleteMetadataField() {
        testFieldGroup = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("Document repository is empty.", 1, metadataFieldGroupRepo.count());
        metadataFieldGroupRepo.delete(testFieldGroup);
        assertEquals("Test Document was not removed.", 0, metadataFieldGroupRepo.count());
    }

    @Test
    @Transactional
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
