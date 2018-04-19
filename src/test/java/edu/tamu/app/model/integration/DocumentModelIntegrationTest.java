package edu.tamu.app.model.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.app.model.AbstractModelTest;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.Resource;

public class DocumentModelIntegrationTest extends AbstractModelTest {

    @Before
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
        assertEquals("Test Document did not have the expected number of resources!", 2, testResources.size());

        assertEquals("Test Document first Resource did not have the expected name!", mockResource1.getName(), testResources.get(0).getName());
        assertEquals("Test Document first Resource did not have the expected path!", mockResource1.getPath(), testResources.get(0).getPath());
        assertEquals("Test Document first Resource did not have the expected url!", mockResource1.getUrl(), testResources.get(0).getUrl());
        assertEquals("Test Document first Resource did not have the expected mime type!", mockResource1.getMimeType(), testResources.get(0).getMimeType());

        assertEquals("Test Document second Resource did not have the expected name!", mockResource2.getName(), testResources.get(1).getName());
        assertEquals("Test Document second Resource did not have the expected path!", mockResource2.getPath(), testResources.get(1).getPath());
        assertEquals("Test Document second Resource did not have the expected url!", mockResource2.getUrl(), testResources.get(1).getUrl());
        assertEquals("Test Document second Resource did not have the expected mime type!", mockResource2.getMimeType(), testResources.get(1).getMimeType());
    }

}
