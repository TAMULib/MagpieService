package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import edu.tamu.app.model.Project;
import edu.tamu.framework.enums.ApiResponseType;

public class ProjectControllerTest extends AbstractControllerTest {

    @Test
    public void testGetProjects() throws Exception {
        response = projectController.getProjects();
        assertEquals(" The response was not successful ", ApiResponseType.SUCCESS, response.getMeta().getType());
        @SuppressWarnings("unchecked")
        List<Project> list = (List<Project>) response.getPayload().get("ArrayList<Project>");
        assertEquals(" There were no projects in the list ", mockProjectList.size(), list.size());
    }

    @Test
    public void testPublicBatch() throws Exception {
        response = projectController.publishBatch(TEST_PROJECT1.getId(), 1l);
        assertEquals(" The response was successful ", ApiResponseType.ERROR, response.getMeta().getType());
        assertEquals(" The response message was a successful one ", "There was an error with the batch publish", response.getMeta().getMessage());

    }

}
