package edu.tamu.app.controllers.interceptors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.app.controllers.AbstractControllerTest;
import edu.tamu.framework.model.Credentials;

public class AppStompInterceptorTest extends AbstractControllerTest {

	@Test
	public void testAnonymousCredentials() {
		Credentials testCredentials = appStompInterceptor.getAnonymousCredentials();
		assertEquals(" The credentials are not for anonymous user last name ", "Anonymous" , testCredentials.getLastName());
		assertEquals(" The credentials are not for anonymous user first name ", "Role" , testCredentials.getFirstName());
		assertEquals(" The credentials are not for anonymous user uin ", "000000000" , testCredentials.getUin());
		assertEquals(" The credentials are not for anonymous user exp ", "1436982214754" , testCredentials.getExp());
		assertEquals(" The credentials are not for anonymous user email ", "helpdesk@library.tamu.edu" , testCredentials.getEmail());
		assertEquals(" The credentials are not for anonymous user role ", "ROLE_ANONYMOUS" , testCredentials.getRole());
	}

	@Test
	public void testConfirmCreateUser() {
		Credentials testCredentials = appStompInterceptor.confirmCreateUser(credentials);
		assertEquals(" User with wrong uin was created ", credentials.getUin() , testCredentials.getUin());
		assertEquals(" User with wrong first name was created ", credentials.getFirstName() , testCredentials.getFirstName());
		assertEquals(" User with wrong last name was created ", credentials.getLastName() , testCredentials.getLastName());
		assertEquals(" the user does not have thr right role ", credentials.getRole() , testCredentials.getRole());
	}
}
