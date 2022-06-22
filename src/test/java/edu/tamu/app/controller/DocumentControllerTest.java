package edu.tamu.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.tamu.app.model.Document;
import edu.tamu.weaver.response.ApiStatus;

public class DocumentControllerTest extends AbstractControllerTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testAllDocuments() {
        response = documentController.allDocuments();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        List<Document> list = (List<Document>) response.getPayload().get("ArrayList<Document>");
        assertEquals(mockDocumentList.size(), list.size(), " The list had no documents in it ");
    }

    @Test
    public void testDocumentByNameandProjectName() {
        response = documentController.documentByName(TEST_PROJECT1.getName(), TEST_DOCUMENT1.getName());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        Document document = (Document) response.getPayload().get("Document");
        assertEquals(TEST_DOCUMENT1.getName(), document.getName(), " The document has a different document document name ");
        assertEquals(TEST_PROJECT1.getName(), document.getProject().getName(), " The document has a different project name ");
    }

    @Test
    public void testPageDocuments() {

    }

    @Test
    public void testSaveDocument() {
        response = documentController.save(TEST_DOCUMENT1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
    }

    @Test
    public void testPushDocument() {
        response = documentController.push(TEST_DOCUMENT1.getProject().getName(), TEST_DOCUMENT1.getName());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        assertEquals("Your item has been successfully published", response.getMeta().getMessage(), " The document was not published ");
        Document document = (Document) response.getPayload().get("Document");
        assertEquals(TEST_DOCUMENT1.getName(), document.getName(), " The document has a different document name ");
        assertEquals(TEST_PROJECT1.getName(), document.getProject().getName(), " The document has a different project name ");
        assertEquals(false, document.isPublishing(), " The document has isPublishing set to TRUE ");
    }

    @Test
    public void testPushAlreadyPublishingDocument() {
        response = documentController.push(TEST_DOCUMENT4.getProject().getName(), TEST_DOCUMENT4.getName());
        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus(), " The response did not error ");
        assertEquals("Cannot publish because document is already pending publication", response.getMeta().getMessage(), " The document did not error due to pending publications ");
        Document document = documentRepo.getById(TEST_DOCUMENT4.getId());
        assertEquals(true, document.isPublishing(), " The document has isPublishing set to FALSE ");
    }

    @Test
    public void testRemoveDocument() {
        response = documentController.remove(TEST_DOCUMENT1.getProject().getName(), TEST_DOCUMENT1.getName());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        String responseMessage = "Document " + TEST_DOCUMENT1.getName() + " has been removed (deleted) from project " + TEST_DOCUMENT1.getProject().getName();
        assertEquals(responseMessage, response.getMeta().getMessage(), " The document was not removed (deleted)");
    }

}
