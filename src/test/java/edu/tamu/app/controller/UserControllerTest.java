package edu.tamu.app.controller;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Role;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiStatus;

@SuppressWarnings("unchecked")
public class UserControllerTest extends AbstractControllerTest {

    @Test
    public void testCredentials() {
        response = userController.credentials(credentials);
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        Credentials userCredentials = (Credentials) response.getPayload().get("Credentials");
        assertEquals(" The user credentials are wrong", TEST_USER1.getFirstName(), userCredentials.getFirstName());
    }

    @Test
    public void testAllUsers() {
        response = userController.allUsers();
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        List<AppUser> list = (List<AppUser>) response.getPayload().get("ArrayList<AppUser>");
        assertEquals(" There are no app users in the list ", mockUserList.size(), list.size());

    }

    @Test
    public void testUpdateRole() {
        TEST_USER1.setRole(Role.ROLE_ANONYMOUS);
        response = userController.update(TEST_USER1);
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
        AppUser user = (AppUser) response.getPayload().get("AppUser");
        assertEquals(" The app user role was not updated ", TEST_USER1.getRole(), user.getRole());
    }

    @Test
    public void testDeleteUser() throws Exception {
        response = userController.delete(TEST_USER3);
        assertEquals(" The response was not successful ", ApiStatus.SUCCESS, response.getMeta().getStatus());
    }

}
