package edu.tamu.app.controllers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import edu.tamu.app.model.Project;
import edu.tamu.framework.enums.ApiResponseType;

public class ProjectControllerTest extends AbstractControllerTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testAllProjects() {
		response = projectController.getProjects();
		assertEquals(" The response was not successful " , ApiResponseType.SUCCESS, response.getMeta().getType());
		List<Project> list = (List<Project>) response.getPayload().get("ArrayList<Project>");
		assertEquals(" The project list was emplty ", mockProjectList.size() , list.size());
	}

	@Test
	public void testPublicBatch() throws Exception {
		response = projectController.publishBatch(TEST_PROJECT1.getId(), 1l);
		assertEquals(" The response was successful " , ApiResponseType.ERROR, response.getMeta().getType());
		assertEquals(" The response message was a successful one ", "There was an error with the batch publish" , response.getMeta().getMessage());
		//TODO ProjectRepository is not defined
	}
}
