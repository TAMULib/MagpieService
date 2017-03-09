package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.tamu.framework.enums.ApiResponseType;

public class ControlledVocabularyControllerTest extends AbstractControllerTest {
	@SuppressWarnings("unchecked")
	@Test
	public void testAllControlledVocabulary() throws Exception {
		response = controlledVocabularyController.getAllControlledVocabulary();
		assertEquals(" The response was not successful " , ApiResponseType.SUCCESS, response.getMeta().getType());
		Map<String,Object> map = (Map<String, Object>) response.getPayload().get("HashMap");
		assertEquals(" The response does not contain the grantor List ", grantorList.size() ,((List<String>) map.get("grantor")).size() );
		assertEquals(" The response does not contain the degree List ", degreeList.size() ,((List<String>) map.get("degrees")).size() );
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testControlledVocabularyByField() throws Exception {
		response = controlledVocabularyController.getControlledVocabularyByField("grantor");
		Map<String,Object> map = (Map<String, Object>) response.getPayload().get("HashMap");
		assertEquals(" The response does not contain the grantor List ", grantorList.size() ,((List<String>) map.get("grantor")).size() );
	}
}
