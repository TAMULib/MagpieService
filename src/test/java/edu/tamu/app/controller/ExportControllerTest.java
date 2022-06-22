package edu.tamu.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.tamu.weaver.response.ApiStatus;

@SuppressWarnings("unchecked")
public class ExportControllerTest extends AbstractControllerTest {

    private static List<String> responseList;

    @Test
    public void testRandomMetadataHeaderFormat() {
        response = exportController.getMetadataHeaders(TEST_PROJECT1.getName(), "randomFormat");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not suucessful ");
        responseList = (List<String>) response.getPayload().get("ArrayList");
        assertEquals(0, responseList.size(), " The list of metadataheaders for random format is not empty ");
    }

    @Test
    public void testSpotlightExportedMetadataHeaders() {
        response = exportController.getMetadataHeaders(TEST_PROJECT1.getName(), "spotlight-csv");
        responseList = (List<String>) response.getPayload().get("ArrayList<String>");
        assertEquals(mockSpotlightExportedMetadataHeaders.size(), responseList.size(), " The spotlight exported metadataheaders list is empty ");
    }

    @Test
    public void testDspaceCsvExportedMetadataHeader() {
        response = exportController.getMetadataHeaders(TEST_PROJECT1.getName(), "dspace-csv");
        responseList = (List<String>) response.getPayload().get("ArrayList<String>");
        assertEquals(mockDspaceCSVExportedMetadataHeaders.size(), responseList.size(), " The spotlight exported metadataheaders list is empty ");
    }

    @Test
    public void testSpotlightCsvExport() {
        response = exportController.spotlightCsvExport(TEST_PROJECT1.getName());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not suucessful ");
        List<List<String>> responseList = (List<List<String>>) response.getPayload().get("ArrayList<ArrayList>");
        assertNotNull(responseList.size(), " The spotlight exported metadata list is empty ");
    }

    @Test
    public void testDSpaceCsvExport() {
        response = exportController.dspaceCsvExport(TEST_PROJECT1.getName());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not suucessful ");
        List<List<String>> responseList = (List<List<String>>) response.getPayload().get("ArrayList<ArrayList>");
        assertNotNull(responseList.size(), " The spotlight exported metadata list is empty ");
    }

    @Test
    public void testSafExport() throws Exception {

    }

    @Test
    public void testPublished() {
        response = metadataController.published(TEST_DOCUMENT1.getStatus());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not suucessful ");

    }

    @Test
    public void testAllMetadataFieldGroup() {
        response = metadataController.all();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not suucessful ");
        Map<String, Object> map = (Map<String, Object>) response.getPayload().get("HashMap");
        List<Object> list = (List<Object>) map.get("list");
        assertEquals(mockMetadataFieldGroupList.size(), list.size(), " The metadataField Group list is empty ");
    }
}
