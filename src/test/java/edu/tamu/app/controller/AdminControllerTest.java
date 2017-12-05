package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import edu.tamu.weaver.response.ApiStatus;

public class AdminControllerTest extends AbstractControllerTest {

    @Test
    public void testSyncDocuments() throws IOException {
        response = adminController.syncDocuments();
        assertEquals(" The response was not suucessful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

}
