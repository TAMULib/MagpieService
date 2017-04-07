package edu.tamu.app.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.tamu.framework.enums.ApiResponseType;

@SuppressWarnings("unchecked")
public class ControlledVocabularyControllerTest extends AbstractControllerClass {

	@Test
	public void testAllControlledVocabulary() throws Exception {
		response = controlledVocabularyController.getAllControlledVocabulary();
		assertEquals(" The response was not successful " , ApiResponseType.SUCCESS, response.getMeta().getType());

		Map<String,Object> map = (Map<String, Object>) response.getPayload().get("LinkedHashMap");
		assertNotNull(" The degree grantor list is null ", ((List<String>) map.get("thesis.degree.grantor")).size());
		assertNotNull(" The degree name list is null ", ((List<String>) map.get("thesis.degree.name")).size());
	}

	@Test
	public void testControlledVocabularyByField() {
		response = controlledVocabularyController.getControlledVocabularyByField("thesis.degree.grantor");
		assertEquals(" The response was not successful " , ApiResponseType.SUCCESS, response.getMeta().getType());
		List<String> degreeGrantorList = (List<String>)response.getPayload().get("ArrayList<String>");
		assertNotNull(" The degree grantor list is null ", degreeGrantorList.size());
	}
}
