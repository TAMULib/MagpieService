package edu.tamu.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.tamu.weaver.response.ApiStatus;

@SuppressWarnings("unchecked")
public class MetadataControllerTest extends AbstractControllerTest {

    @Test
    public void testUnlockProject() {
        response = metadataController.unlockProject(TEST_PROJECT1.getName());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not suucessful ");
    }

    @Test
    public void testPublished() {
        response = metadataController.published(TEST_DOCUMENT1.getStatus());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not suucessful ");

    }

    @Test
    public void testAllMetadataFieldGroup() {
        response = metadataController.all();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not suucessful ");
        Map<String, Object> map = (Map<String, Object>) response.getPayload().get("HashMap");
        List<Object> list = (List<Object>) map.get("list");
        assertEquals(mockMetadataFieldGroupList.size(), list.size(), " The metadataField Group list is empty ");
    }
}
