package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.tamu.weaver.response.ApiStatus;

@SuppressWarnings("unchecked")
public class MetadataControllerTest extends AbstractControllerTest {

    @Test
    public void testUnlockProject() {
        response = metadataController.unlockProject(TEST_PROJECT1.getName());
        assertEquals(" The response was not suucessful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
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
