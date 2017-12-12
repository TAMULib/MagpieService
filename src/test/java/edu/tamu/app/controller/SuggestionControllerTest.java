package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.weaver.response.ApiStatus;

public class SuggestionControllerTest extends AbstractControllerTest {

    @Test
    public void testGetSuggestions() throws Exception {
        response = suggestionController.getSuggestions(TEST_PROJECT1.getName(), TEST_DOCUMENT1.getName());
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());

    }
}
