package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.tamu.app.model.Project;
import edu.tamu.weaver.response.ApiStatus;

public class ProjectControllerTest extends AbstractControllerTest {

    @Test
    public void testGetProjects() throws Exception {
        response = projectController.getProjects();
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        @SuppressWarnings("unchecked")
        List<Project> list = (List<Project>) response.getPayload().get("ArrayList<Project>");
        assertEquals(" There were no projects in the list ", mockProjectList.size(), list.size());
    }

    @Test
    public void testPublicBatch() throws Exception {
        response = projectController.publishBatch(TEST_PROJECT1.getId(), 1l);
        assertEquals(" The response was successful ", ApiStatus.ERROR, response.getMeta().getStatus());
        assertEquals(" The response message was a successful one ", "There was an error with the batch publish", response.getMeta().getMessage());

    }

    @Test
    public void testAddFieldProfile() throws Exception {
        JsonNode data = getFieldProfileData();
        response = projectController.addFieldProfile(new Long(1L), data);
        assertEquals(" The Field Profile was added ", ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

    @Test
    public void testUpdateFieldProfile() throws Exception {
        JsonNode data = getFieldProfileData();
        response = projectController.updateFieldProfile(new Long(1L), data);
        assertEquals(" The Field Profile was updated ", ApiStatus.SUCCESS, response.getMeta().getStatus());
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

}
