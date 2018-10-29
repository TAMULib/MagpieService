package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import edu.tamu.weaver.response.ApiStatus;

public class AdminControllerTest extends AbstractControllerTest {

    @Test
    public void testSyncDocuments(Long projectId) throws IOException {
        response = projectController.syncDocuments(projectId);
        assertEquals(" The response was not suucessful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

}
