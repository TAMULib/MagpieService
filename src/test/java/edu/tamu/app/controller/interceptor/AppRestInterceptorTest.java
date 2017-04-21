package edu.tamu.app.controller.interceptor;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import edu.tamu.app.controller.AbstractControllerTest;
import edu.tamu.framework.model.Credentials;

public class AppRestInterceptorTest extends AbstractControllerTest {

	@Test
	public void testAnonymousCredentials() {
		testCredentials = appRestInterceptor.getAnonymousCredentials();
		assertEquals(" The credentials are not for anonymous user last name ", "Anonymous" , testCredentials.getLastName());
		assertEquals(" The credentials are not for anonymous user first name ", "Role" , testCredentials.getFirstName());
		assertEquals(" The credentials are not for anonymous user uin ", "000000000" , testCredentials.getUin());
		assertEquals(" The credentials are not for anonymous user exp ", "1436982214754" , testCredentials.getExp());
		assertEquals(" The credentials are not for anonymous user email ", "helpdesk@library.tamu.edu" , testCredentials.getEmail());
		assertEquals(" The credentials are not for anonymous user role ", "ROLE_ANONYMOUS" , testCredentials.getRole());
	}

	@Test
	public void testConfirmCreateUser() {
		Credentials credentials = new Credentials();
		credentials.setFirstName(aggieJackToken.get("firstName"));
		credentials.setLastName(aggieJackToken.get("lastName"));
		credentials.setNetid(aggieJackToken.get("netid"));
		credentials.setUin(aggieJackToken.get("uin"));
		testCredentials = appRestInterceptor.confirmCreateUser(credentials);
		assertEquals(" The credentials for created user last name is incorrect", aggieJackToken.get("lastName") , testCredentials.getLastName());
		assertEquals(" The credentials for created user first name is incorrect ", aggieJackToken.get("firstName") , testCredentials.getFirstName());
		assertEquals(" The credentials for created user netid is incorrect ", aggieJackToken.get("netid") , testCredentials.getNetid());
		assertEquals(" The credentials for created user uin is incorrect ", aggieJackToken.get("uin") , testCredentials.getUin());
		assertEquals(" The credentials for created user exp is incorrect ", null , testCredentials.getExp());
	}

	@Test
	public void testCreateUser() {
		credentials = new Credentials();
		credentials.setUin("99");
		credentials.setAllCredentials(new HashMap<String, String>());
		testCredentials = appRestInterceptor.confirmCreateUser(credentials);
		assertEquals(" The credentials for created user role is incorrect ", "ROLE_USER" , testCredentials.getRole());
	}

}
