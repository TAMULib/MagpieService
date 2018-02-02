package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.tamu.weaver.response.ApiStatus;

@SuppressWarnings("unchecked")
public class ExportControllerTest extends AbstractControllerTest {

    private static List<String> responseList;

    @Test
    public void testRandomMetadataHeaderFormat() {
        response = exportController.getMetadataHeaders(TEST_PROJECT1.getName(), "randomFormat");
        assertEquals(" The response was not suucessful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        responseList = (List<String>) response.getPayload().get("ArrayList");
        assertEquals(" The list of metadataheaders for random format is not empty ", 0, responseList.size());
    }

    @Test
    public void testSpotlightExportedMetadataHeaders() {
        response = exportController.getMetadataHeaders(TEST_PROJECT1.getName(), "spotlight-csv");
        responseList = (List<String>) response.getPayload().get("ArrayList<String>");
        assertEquals(" The spotlight exported metadataheaders list is empty ", mockSpotlightExportedMetadataHeaders.size(), responseList.size());
    }

    @Test
    public void testDspaceCsvExportedMetadataHeader() {
        response = exportController.getMetadataHeaders(TEST_PROJECT1.getName(), "dspace-csv");
        responseList = (List<String>) response.getPayload().get("ArrayList<String>");
        assertEquals(" The spotlight exported metadataheaders list is empty ", mockDspaceCSVExportedMetadataHeaders.size(), responseList.size());
    }

    @Test
    public void testSpotlightCsvExport() {
        response = exportController.spotlightCsvExport(TEST_PROJECT1.getName());
        assertEquals(" The response was not suucessful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        List<List<String>> responseList = (List<List<String>>) response.getPayload().get("ArrayList<ArrayList>");
        assertNotNull(" The spotlight exported metadata list is empty ", responseList.size());
    }

    @Test
    public void testDSpaceCsvExport() {
        response = exportController.dspaceCsvExport(TEST_PROJECT1.getName());
        assertEquals(" The response was not suucessful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        List<List<String>> responseList = (List<List<String>>) response.getPayload().get("ArrayList<ArrayList>");
        assertNotNull(" The spotlight exported metadata list is empty ", responseList.size());
    }

    @Test
    public void testSafExport() throws Exception {

    }

    @Test
    public void testPublished() {
        response = metadataController.published(TEST_DOCUMENT1.getStatus());
        assertEquals(" The response was not suucessful ", ApiStatus.SUCCESS, response.getMeta().getStatus());

    }

    @Test
    public void testAllMetadataFieldGroup() {
        response = metadataController.all();
        assertEquals(" The response was not suucessful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        Map<String, Object> map = (Map<String, Object>) response.getPayload().get("HashMap");
        List<Object> list = (List<Object>) map.get("list");
        assertEquals(" The metadataField Group list is empty ", mockMetadataFieldGroupList.size(), list.size());
    }
}
