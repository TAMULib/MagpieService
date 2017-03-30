package edu.tamu.app.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tamu.app.enums.AppRole;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Project;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

public class MockData {

	protected static ApiResponse response;

	protected static Credentials credentials = new Credentials();

	protected static AppUser TEST_USER1 = new AppUser(123456789l, "Jane", "Daniel", AppRole.ROLE_ADMIN.toString());
	protected static AppUser TEST_USER2 = new AppUser(234567891l, "Jane", "Daniel", AppRole.ROLE_MANAGER.toString());
	protected static AppUser TEST_USER3 = new AppUser(345678912l, "Jane", "Daniel", AppRole.ROLE_USER.toString());
	protected static List<AppUser> mockAppUserList = new ArrayList<AppUser>(Arrays.asList(new AppUser[] {TEST_USER1 , TEST_USER2 , TEST_USER3}));

	static {
		TEST_USER1.setId(1l);
		TEST_USER2.setId(2l);
		TEST_USER3.setId(3l);
		//credentials
		credentials.setAffiliation("TAMU_LIB");
		credentials.setFirstName(TEST_USER1.getFirstName());
		credentials.setLastName(TEST_USER1.getLastName());
		credentials.setEmail("aggiejack@tamu.edu");
		credentials.setNetid("aggiejack");
		credentials.setRole("ROLE_USER");
		credentials.setUin(TEST_USER1.getUin().toString());
	}

	public AppUser saveAppUser(AppUser modifiedUser) {
		for(AppUser user : mockAppUserList) {
			if(user.getUin().equals(modifiedUser.getUin())) {
				user.setFirstName(modifiedUser.getFirstName());
				user.setLastName(modifiedUser.getLastName());
				user.setRole(modifiedUser.getRole());
				return user;
			}
		}
		return null;
	}

	protected static Map<String, String> aggieJackToken;

	//Project
	protected static Project TEST_PROJECT1 = new Project("Project Name 1");
	protected static Project TEST_PROJECT2 = new Project("Project Name 2");
	protected static Project TEST_PROJECT3 = new Project("Project Name 3");

	static {
		TEST_PROJECT1.setId(1l);
		TEST_PROJECT2.setId(2l);
		TEST_PROJECT3.setId(3l);
	}

	protected static List<Project> mockProjectList = new ArrayList<Project>(Arrays.asList(new Project[] {TEST_PROJECT1 , TEST_PROJECT2 , TEST_PROJECT3}));

	protected static List<String> grantorList = new ArrayList<String>();
	protected static List<String> degreeList = new ArrayList<String>();

	protected static Map<String, Object> cvMap = new HashMap<String, Object>();

}
