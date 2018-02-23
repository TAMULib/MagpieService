package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

public class DocumentTest extends AbstractModelTest {

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        mockDocument = new Document(testProject, "testDocument", "documentPath", "Unassigned");
        assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
    }

    @Test
    public void testCreateDocument() {
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        assertEquals("Test Document was not created.", 1, documentRepo.count());
        assertEquals("Expected Test Document was not created.", mockDocument.getName(), testDocument.getName());
    }

    @Test
    public void testFindDocument() {
        assertEquals("Test Document already exists.", null, documentRepo.findByProjectNameAndName(testProject.getName(), "testFile"));
        documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        testDocument = documentRepo.findByProjectNameAndName(mockDocument.getProject().getName(), mockDocument.getName());
        assertEquals("Test Document was not found.", mockDocument.getName(), testDocument.getName());
    }

    @Test
    @Transactional
    public void testDeleteDocument() {
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        assertEquals("DocumentRepo is empty.", 1, documentRepo.count());
        documentRepo.delete(testDocument);
        assertEquals("Test Document was not removed.", 0, documentRepo.count());
    }

    @Test
    @Transactional
    public void testCascadeOnDeleteDocument() {
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        assertEquals("Test Document was not created.", 1, documentRepo.count());

        assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
        FieldProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());

        assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
        MetadataFieldLabel testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        assertEquals("Test MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());

        assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldGroupRepo.count());
        MetadataFieldGroup testField = metadataFieldGroupRepo.create(testDocument, testLabel);
        assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());

        assertEquals("MetadataFieldValue repository is not empty.", 0, metadataFieldValueRepo.count());
        MetadataFieldValue fieldValue = metadataFieldValueRepo.create("test", testField);
        assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());

        testField.addValue(fieldValue);

        testDocument.addField(testField);

        testDocument = documentRepo.save(testDocument);

        assertEquals("Test Document was not created.", 1, documentRepo.count());

        assertEquals("Test Document had an incorrect number of fields.", 1, testDocument.getFields().size());

        assertEquals("Test Document's field had an incorrect number of values.", 1, testDocument.getFields().get(0).getValues().size());

        documentRepo.delete(testDocument);

        assertEquals("Test Document was not deleted.", 0, documentRepo.count());

        assertEquals("Test MetadataFieldLabel was deleted.", 1, metadataFieldLabelRepo.count());

        assertEquals("Test ProjectFieldProfile was deleted.", 1, projectFieldProfileRepo.count());

        assertEquals("Test MetadataField was not deleted.", 0, metadataFieldGroupRepo.count());

        assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
    }

    @Test
    public void testDocumentSetters() {
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        testDocument.setName("Another name for test Document");
        testDocument.setStatus("Assigned");
        testDocument.setAnnotator("An Annotator");
        testDocument.setNotes("Notes for Test Document");
        assertEquals(" The document name was not modified ", "Another name for test Document", testDocument.getName());
        assertEquals(" The document name was not modified ", "An Annotator", testDocument.getAnnotator());
        assertEquals(" The document name was not modified ", "Notes for Test Document", testDocument.getNotes());
    }

}
