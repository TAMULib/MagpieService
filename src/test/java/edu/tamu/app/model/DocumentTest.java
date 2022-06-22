package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

public class DocumentTest extends AbstractModelTest {

    @BeforeEach
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        mockDocument = new Document(testProject, "testDocument", "documentPath", "Unassigned");
        assertEquals(0, documentRepo.count(), "DocumentRepo is not empty.");
    }

    @Test
    public void testCreateDocument() {
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        assertEquals(1, documentRepo.count(), "Test Document was not created.");
        assertEquals(mockDocument.getName(), testDocument.getName(), "Expected Test Document was not created.");
    }

    @Test
    public void testDuplicateDocument() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
            documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        });
    }

    @Test
    public void testFindDocument() {
        assertEquals(null, documentRepo.findByProjectNameAndName(testProject.getName(), "testFile"), "Test Document already exists.");
        documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        testDocument = documentRepo.findByProjectNameAndName(mockDocument.getProject().getName(), mockDocument.getName());
        assertEquals(mockDocument.getName(), testDocument.getName(), "Test Document was not found.");
    }

    @Test
    @Transactional
    public void testDeleteDocument() {
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        assertEquals(1, documentRepo.count(), "DocumentRepo is empty.");
        documentRepo.delete(testDocument);
        assertEquals(0, documentRepo.count(), "Test Document was not removed.");
    }

    @Test
    @Transactional
    public void testCascadeOnDeleteDocument() {
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        assertEquals(1, documentRepo.count(), "Test Document was not created.");

        assertEquals(0, projectFieldProfileRepo.count(), "ProjectFieldProfileRepo is not empty.");
        FieldProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals(1, projectFieldProfileRepo.count(), "Test ProjectFieldProfile was not created.");

        assertEquals(0, metadataFieldLabelRepo.count(), "MetadataFieldLabelRepo is not empty.");
        MetadataFieldLabel testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        assertEquals(1, metadataFieldLabelRepo.count(), "Test MetadataFieldLabel was not created.");

        assertEquals(0, metadataFieldGroupRepo.count(), "MetadataFieldRepo is not empty.");
        MetadataFieldGroup testField = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals(1, metadataFieldGroupRepo.count(), "Test MetadataField was not created.");

        assertEquals(0, metadataFieldValueRepo.count(), "MetadataFieldValue repository is not empty.");
        MetadataFieldValue fieldValue = metadataFieldValueRepo.create("test", testField);
        assertEquals(1, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not created.");

        testField.addValue(fieldValue);

        testDocument.addField(testField);

        testDocument = documentRepo.save(testDocument);

        assertEquals(1, documentRepo.count(), "Test Document was not created.");

        assertEquals(1, testDocument.getFields().size(), "Test Document had an incorrect number of fields.");

        assertEquals(1, testDocument.getFields().get(0).getValues().size(), "Test Document's field had an incorrect number of values.");

        documentRepo.delete(testDocument);

        assertEquals(0, documentRepo.count(), "Test Document was not deleted.");

        assertEquals(1, metadataFieldLabelRepo.count(), "Test MetadataFieldLabel was deleted.");

        assertEquals(1, projectFieldProfileRepo.count(), "Test ProjectFieldProfile was deleted.");

        assertEquals(0, metadataFieldGroupRepo.count(), "Test MetadataField was not deleted.");

        assertEquals(0, metadataFieldValueRepo.count(), "Test MetadataFieldValue was not deleted.");
    }

    @Test
    public void testDocumentSetters() {
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        testDocument.setName("Another name for test Document");
        testDocument.setStatus("Assigned");
        testDocument.setAnnotator("An Annotator");
        testDocument.setNotes("Notes for Test Document");
        assertEquals("Another name for test Document", testDocument.getName(), " The document name was not modified ");
        assertEquals("An Annotator", testDocument.getAnnotator(), " The document name was not modified ");
        assertEquals("Notes for Test Document", testDocument.getNotes(), " The document name was not modified ");
    }

}
