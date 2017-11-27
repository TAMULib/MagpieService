package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.tamu.weaver.response.ApiStatus;

@SuppressWarnings("unchecked")
public class ControlledVocabularyControllerTest extends AbstractControllerTest {

    @Test
    public void testAllControlledVocabulary() {
        response = controlledVocabularyController.getAllControlledVocabulary();
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        Map<String, Object> map = (Map<String, Object>) response.getPayload().get("LinkedHashMap");
        assertNotNull(" The degree grantor list is null ", ((List<String>) map.get("thesis.degree.grantor")).size());
        assertNotNull(" The degree name list is null ", ((List<String>) map.get("thesis.degree.name")).size());
    }

    @Test
    public void testControlledVocabularyByField() {
        response = controlledVocabularyController.getControlledVocabularyByField("thesis.degree.grantor");
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        List<String> degreeGrantorList = (List<String>) response.getPayload().get("ArrayList<String>");
        assertNotNull(" The degree grantor list is null ", degreeGrantorList.size());
    }

}
