package edu.tamu.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.tamu.app.model.Project;
import edu.tamu.weaver.response.ApiStatus;

public class ProjectControllerTest extends AbstractControllerTest {

    @Test
    public void testGetProjects() throws Exception {
        response = projectController.getProjects();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        @SuppressWarnings("unchecked")
        List<Project> list = (List<Project>) response.getPayload().get("ArrayList<Project>");
        assertEquals(mockProjectList.size(), list.size(), " There were no projects in the list ");
    }

    @Test
    public void testPublicBatch() throws Exception {
        response = projectController.publishBatch(TEST_PROJECT1.getId(), 1l);
        assertEquals(ApiStatus.ERROR, response.getMeta().getStatus(), " The response was successful ");
        assertEquals("There was an error with the batch publish", response.getMeta().getMessage(), " The response message was a successful one ");

    }

    @Test
    public void testAddFieldProfile() throws Exception {
        JsonNode data = getFieldProfileData();
        response = projectController.addFieldProfile(Long.valueOf(1L), data);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The Field Profile was added ");
    }

    @Test
    public void testUpdateFieldProfile() throws Exception {
        JsonNode data = getFieldProfileData();
        response = projectController.updateFieldProfile(Long.valueOf(1L), data);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The Field Profile was updated ");
    }

    private JsonNode getFieldProfileData() {
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.set("fieldProfile", objectMapper.valueToTree(TEST_PROFILE1));

        List<Map<String,String>> labels = new ArrayList<Map<String,String>>();
        Map<String,String> labelMap = new HashMap<String,String>();
        labelMap.put("name", TEST_META_LABEL.getName());
        labels.add(labelMap);

        data.set("labels", objectMapper.valueToTree(labels));
        return data;
    }

    @Test
    public void testSyncDocuments() throws IOException {
        response = projectController.syncDocuments(TEST_PROJECT1.getId());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
    }

}
