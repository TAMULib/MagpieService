package edu.tamu.app.model.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.tamu.app.model.AbstractModelTest;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.Resource;

public class DocumentModelIntegrationTest extends AbstractModelTest {

    @BeforeEach
    public void setUp() {
        mockDocument = new Document(testProject, "testDocument", "documentPath", "Unassigned");
        mockResource1 = new Resource(mockDocument, "testResource1", "resourcePath1", "mime/type1");
        mockResource2 = new Resource(mockDocument, "testResource2", "resourcePath2", "mime/type2");
    }

    @Test
    public void testCreateDocumentResources() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);

        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());

        resourceRepo.create(new Resource(testDocument, mockResource1.getName(), mockResource1.getPath(), mockResource1.getMimeType()));

        resourceRepo.create(new Resource(testDocument, mockResource2.getName(), mockResource2.getPath(), mockResource2.getMimeType()));

        List<Resource> testResources = resourceRepo.findAllByDocumentProjectNameAndDocumentName(testProject.getName(), testDocument.getName());
        assertEquals(2, testResources.size(), "Test Document did not have the expected number of resources!");

        assertEquals(mockResource1.getName(), testResources.get(0).getName(), "Test Document first Resource did not have the expected name!");
        assertEquals(mockResource1.getPath(), testResources.get(0).getPath(), "Test Document first Resource did not have the expected path!");
        assertEquals(mockResource1.getUrl(), testResources.get(0).getUrl(), "Test Document first Resource did not have the expected url!");
        assertEquals(mockResource1.getMimeType(), testResources.get(0).getMimeType(), "Test Document first Resource did not have the expected mime type!");

        assertEquals(mockResource2.getName(), testResources.get(1).getName(), "Test Document second Resource did not have the expected name!");
        assertEquals(mockResource2.getPath(), testResources.get(1).getPath(), "Test Document second Resource did not have the expected path!");
        assertEquals(mockResource2.getUrl(), testResources.get(1).getUrl(), "Test Document second Resource did not have the expected url!");
        assertEquals(mockResource2.getMimeType(), testResources.get(1).getMimeType(), "Test Document second Resource did not have the expected mime type!");
    }

}
