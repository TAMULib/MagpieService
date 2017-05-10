package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.framework.enums.ApiResponseType;

public class SuggestionControllerTest extends AbstractControllerTest {

    @Test
    public void testGetSuggestions() throws Exception {
        response = suggestionController.getSuggestions(TEST_PROJECT1.getName(), TEST_DOCUMENT1.getName());
        assertEquals(" The response was not successful ", ApiResponseType.SUCCESS, response.getMeta().getType());

    }
}
