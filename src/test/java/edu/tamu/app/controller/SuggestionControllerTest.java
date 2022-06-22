package edu.tamu.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.tamu.weaver.response.ApiStatus;

public class SuggestionControllerTest extends AbstractControllerTest {

    @Test
    public void testGetSuggestions() throws Exception {
        response = suggestionController.getSuggestions(TEST_PROJECT1.getName(), TEST_DOCUMENT1.getName());
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
    }
}
