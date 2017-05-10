package edu.tamu.app.controller.interceptor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.app.controller.AbstractControllerTest;

public class AppStompInterceptorTest extends AbstractControllerTest {

    @Test
    public void testAnonymousCredentials() {
        credentials = appStompInterceptor.getAnonymousCredentials();
        assertEquals(" The credentials are not for anonymous user last name ", "Anonymous", credentials.getLastName());
        assertEquals(" The credentials are not for anonymous user first name ", "Role", credentials.getFirstName());
        assertEquals(" The credentials are not for anonymous user uin ", "000000000", credentials.getUin());
        assertEquals(" The credentials are not for anonymous user exp ", "1436982214754", credentials.getExp());
        assertEquals(" The credentials are not for anonymous user email ", "helpdesk@library.tamu.edu", credentials.getEmail());
        assertEquals(" The credentials are not for anonymous user role ", "ROLE_ANONYMOUS", credentials.getRole());
    }

    @Test
    public void testConfirmCreateUser() {

    }

    @Test
    public void testCreateUser() {

    }
}
