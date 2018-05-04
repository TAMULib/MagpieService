package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import edu.tamu.app.model.Document;
import edu.tamu.weaver.response.ApiStatus;

public class DocumentControllerTest extends AbstractControllerTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testAllDocuments() {
        response = documentController.allDocuments();
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        List<Document> list = (List<Document>) response.getPayload().get("ArrayList<Document>");
        assertEquals(" The list had no documents in it ", mockDocumentList.size(), list.size());
    }

    @Test
    public void testDocumentByNameandProjectName() {
        response = documentController.documentByName(TEST_PROJECT1.getName(), TEST_DOCUMENT1.getName());
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        Document document = (Document) response.getPayload().get("Document");
        assertEquals(" The document has a different document document name ", TEST_DOCUMENT1.getName(), document.getName());
        assertEquals(" The document has a different project name ", TEST_PROJECT1.getName(), document.getProject().getName());
    }

    @Test
    public void testPageDocuments() {

    }

    @Test
    public void testSaveDocument() {
        response = documentController.save(TEST_DOCUMENT1);
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testPushDocument() {
        response = documentController.push(TEST_DOCUMENT1.getProject().getName(), TEST_DOCUMENT1.getName());
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        assertEquals(" The document was not published ", "Your item has been successfully published", response.getMeta().getMessage());
        Document document = (Document) response.getPayload().get("Document");
        assertEquals(" The document has a different document name ", TEST_DOCUMENT1.getName(), document.getName());
        assertEquals(" The document has a different project name ", TEST_PROJECT1.getName(), document.getProject().getName());
    }

    @Test
    public void testRemoveDocument() {
        response = documentController.remove(TEST_DOCUMENT1.getProject().getName(), TEST_DOCUMENT1.getName());
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        String responseMessage = "Document " + TEST_DOCUMENT1.getName() + " has been removed (deleted) from project " + TEST_DOCUMENT1.getProject().getName();
        assertEquals(" The document was not removed (deleted)", responseMessage, response.getMeta().getMessage());
    }

}
