package edu.tamu.app.controllers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import edu.tamu.app.enums.AppRole;
import edu.tamu.app.model.AppUser;
import edu.tamu.framework.enums.ApiResponseType;
import edu.tamu.framework.model.Credentials;

public class UserControllerTest extends AbstractControllerTest {

	@Test
	public void testCredentials() throws Exception {
		response = userController.credentials(credentials);
		assertEquals(" The response was not successful " , ApiResponseType.SUCCESS, response.getMeta().getType());
		Credentials testCredentials = (Credentials) response.getPayload().get("Credentials");
		assertEquals(" The user credential for first name was not correct ", credentials.getFirstName() , testCredentials.getFirstName());
		assertEquals(" The user credential for last name was not correct ", credentials.getLastName() , testCredentials.getLastName());
		assertEquals(" The user credential for role was not correct ", credentials.getRole() , testCredentials.getRole());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAllUsers() {
		response = userController.allUsers();
		assertEquals(" The response was not successful " , ApiResponseType.SUCCESS, response.getMeta().getType());
		List<AppUser> list = (List<AppUser>) response.getPayload().get("ArrayList<AppUser>");
		assertEquals(" There were no users in the repository ", mockUserList.size() , list.size());
	}

	@Test
	public void testUpdateRole() {
		TEST_USER2.setRole(AppRole.ROLE_ADMIN);
		response = userController.updateRole(TEST_USER2);
		AppUser jackUser = (AppUser) response.getPayload().get("AppUser");
		assertEquals(" The role was not updated for Jack ", TEST_USER2.getRole() , jackUser.getRole());
	}

	@Test
	public void testDeleteUser() throws Exception {
		response = userController.delete(TEST_USER2);
		assertEquals(" The response was not successful " , ApiResponseType.SUCCESS , response.getMeta().getType());
	}
}
