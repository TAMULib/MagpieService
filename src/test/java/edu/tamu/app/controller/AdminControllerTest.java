package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.framework.enums.ApiResponseType;

public class AdminControllerTest extends AbstractControllerTest {

	@Test
	public void testSyncDocuments() {
		response = adminController.syncDocuments();
		assertEquals(" The response was not suucessful ", ApiResponseType.SUCCESS , response.getMeta().getType());
	}
}
