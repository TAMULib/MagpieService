package edu.tamu.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.tamu.weaver.response.ApiStatus;

@SuppressWarnings("unchecked")
public class ControlledVocabularyControllerTest extends AbstractControllerTest {

    @Test
    public void testAllControlledVocabulary() {
        response = controlledVocabularyController.getAllControlledVocabulary();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        Map<String, Object> map = (Map<String, Object>) response.getPayload().get("LinkedHashMap");
        assertNotNull(((List<String>) map.get("thesis.degree.grantor")).size(), " The degree grantor list is null ");
        assertNotNull(((List<String>) map.get("thesis.degree.name")).size(), " The degree name list is null ");
    }

    @Test
    public void testControlledVocabularyByField() {
        response = controlledVocabularyController.getControlledVocabularyByField("thesis.degree.grantor");
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        List<String> degreeGrantorList = (List<String>) response.getPayload().get("ArrayList<String>");
        assertNotNull(degreeGrantorList.size(), " The degree grantor list is null ");
    }

}
