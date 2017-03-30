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
		assertEquals(" The response was not successful ", ApiResponseType.SUCCESS , response.getMeta().getType());
		@SuppressWarnings("unchecked")
		List<Project> list = (List<Project>) response.getPayload().get("ArrayList<Project>");
		assertEquals(" There were no projects in the list ", mockProjectList.size() , list.size());
	}
}
