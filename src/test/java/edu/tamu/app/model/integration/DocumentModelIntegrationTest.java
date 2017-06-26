package edu.tamu.app.model.integration;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.app.model.AbstractModelTest;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.Resource;

public class DocumentModelIntegrationTest extends AbstractModelTest {

    @Before
    public void setUp() {
        mockDocument = new Document(testProject, "testDocument", "documentPath", "Unassigned");
        mockResource1 = new Resource("testResource1", "resourcePath1", "resourceUrl1", "mime/type1");
        mockResource2 = new Resource("testResource2", "resourcePath2", "resourceUrl2", "mime/type2");
    }

    @Test
    public void testCreateDocumentResources() {
        testProject = projectRepo.create("testProject");
        
        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getDocumentPath(), mockDocument.getStatus());

        testDocument.addResource(new Resource(mockResource1.getName(), mockResource1.getPath(), mockResource1.getUrl(), mockResource1.getMimeType()));
        
        testDocument.addResource(new Resource(mockResource2.getName(), mockResource2.getPath(), mockResource2.getUrl(), mockResource2.getMimeType()));
        
        testDocument = documentRepo.save(testDocument);
        
        assertEquals("Test Document did not have the expected number of resources!", 2, testDocument.getResources().size());
        
        assertEquals("Test Document first Resource did not have the expected name!", mockResource1.getName(), testDocument.getResources().get(0).getName());
        assertEquals("Test Document first Resource did not have the expected path!", mockResource1.getPath(), testDocument.getResources().get(0).getPath());
        assertEquals("Test Document first Resource did not have the expected url!", mockResource1.getUrl(), testDocument.getResources().get(0).getUrl());
        assertEquals("Test Document first Resource did not have the expected mime type!", mockResource1.getMimeType(), testDocument.getResources().get(0).getMimeType());
        
        assertEquals("Test Document second Resource did not have the expected name!", mockResource2.getName(), testDocument.getResources().get(1).getName());
        assertEquals("Test Document second Resource did not have the expected path!", mockResource2.getPath(), testDocument.getResources().get(1).getPath());
        assertEquals("Test Document second Resource did not have the expected url!", mockResource2.getUrl(), testDocument.getResources().get(1).getUrl());
        assertEquals("Test Document second Resource did not have the expected mime type!", mockResource2.getMimeType(), testDocument.getResources().get(1).getMimeType());
    }

}
