package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.framework.enums.ApiResponseType;

public class MetadataControllerTest extends AbstractControllerTest {

	@Test
	public void testUnlockProject() {
		response  = metadataController.unlockProject(TEST_PROJECT1.getName());
		assertEquals(" The response was not successful ", ApiResponseType.SUCCESS , response.getMeta().getType());
	}

	@Test
	public void testGetMetadataHeaders() {
//		response = metadataController.getMetadataHeaders(project);
	}

}
