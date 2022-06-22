package edu.tamu.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Role;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiStatus;

@SuppressWarnings("unchecked")
public class UserControllerTest extends AbstractControllerTest {

    @Test
    public void testCredentials() {
        response = userController.credentials(credentials);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        Credentials userCredentials = (Credentials) response.getPayload().get("Credentials");
        assertEquals(TEST_USER1.getFirstName(), userCredentials.getFirstName(), " The user credentials are wrong");
    }

    @Test
    public void testAllUsers() {
        response = userController.allUsers();
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        List<AppUser> list = (List<AppUser>) response.getPayload().get("ArrayList<AppUser>");
        assertEquals(mockUserList.size(), list.size(), " There are no app users in the list ");

    }

    @Test
    public void testUpdateRole() {
        TEST_USER1.setRole(Role.ROLE_ANONYMOUS);
        response = userController.update(TEST_USER1);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
        AppUser user = (AppUser) response.getPayload().get("AppUser");
        assertEquals(TEST_USER1.getRole(), user.getRole(), " The app user role was not updated ");
    }

    @Test
    public void testDeleteUser() throws Exception {
        response = userController.delete(TEST_USER3);
        assertEquals(ApiStatus.SUCCESS, response.getMeta().getStatus(), " The response was not successful ");
    }

}
