package edu.tamu.app.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.tamu.framework.enums.ApiResponseType;

@SuppressWarnings("unchecked")
public class MetadataControllerTest extends AbstractControllerTest {

	private static List<String> responseList;

	@Test
	public void testUnlockProject() {
		response = metadataController.unlockProject(TEST_PROJECT1.getName());
		assertEquals(" The response was not suucessful ", ApiResponseType.SUCCESS , response.getMeta().getType());
	}

	@Test
	public void testRandomMetadataHeaderFormat() {
		response = metadataController.getMetadataHeaders(TEST_PROJECT1.getName(), "randomFormat");
		assertEquals(" The response was not suucessful ", ApiResponseType.SUCCESS , response.getMeta().getType());
		responseList = (List<String>) response.getPayload().get("ArrayList");
		assertEquals(" The list of metadataheaders for random format is not empty ", 0, responseList.size());
	}

	@Test
	public void testSpotlightExportedMetadataHeaders() {
		response = metadataController.getMetadataHeaders(TEST_PROJECT1.getName(), "spotlight");
		responseList = (List<String>) response.getPayload().get("ArrayList<String>");
		assertEquals(" The spotlight exported metadataheaders list is empty ", mockSpotlightExportedMetadataHeaders.size() , responseList.size());
	}

	@Test
	public void testDspaceCsvExportedMetadataHeader() {
		response = metadataController.getMetadataHeaders(TEST_PROJECT1.getName(), "dspacecsv");
		responseList = (List<String>) response.getPayload().get("ArrayList<String>");
		assertEquals(" The spotlight exported metadataheaders list is empty ", mockDspaceCSVExportedMetadataHeaders.size(), responseList.size());
	}

	@Test
	public void testGetSpotlightExport() {
		response = metadataController.getSpotlightExport(TEST_PROJECT1.getName());
		assertEquals(" The response was not suucessful ", ApiResponseType.SUCCESS , response.getMeta().getType());
		List<List<String>> responseList = (List<List<String>>) response.getPayload().get("ArrayList<ArrayList>");
		assertNotNull(" The spotlight exported metadata list is empty ", responseList.size());
	}

	@Test
	public void testGetCsvExport() {
		response = metadataController.getCSVByroject(TEST_PROJECT1.getName());
		assertEquals(" The response was not suucessful ", ApiResponseType.SUCCESS , response.getMeta().getType());
		List<List<String>> responseList = (List<List<String>>) response.getPayload().get("ArrayList<ArrayList>");
		assertNotNull(" The spotlight exported metadata list is empty ", responseList.size());
	}

	//TODO
	@Test
	public void testSaf() throws Exception {
//		response = metadataController.saf(TEST_PROJECT1.getName());
	}

	@Test
	public void testPublished() {
		response = metadataController.published(TEST_DOCUMENT1.getStatus());
		assertEquals(" The response was not suucessful ", ApiResponseType.SUCCESS , response.getMeta().getType());
		//TODO mock the List<List<String>>
	}
	
	@Test
	public void testAllMetadataFieldGroup() {
		response = metadataController.all();
		assertEquals(" The response was not suucessful ", ApiResponseType.SUCCESS , response.getMeta().getType());
		Map<String, Object> map = (Map<String, Object>) response.getPayload().get("HashMap");
		List<Object> list = (List<Object>) map.get("list");
		assertEquals(" The metadataField Group list is empty " , mockMetadataFieldGroupList.size() , list.size());
	}
}
