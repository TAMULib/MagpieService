package edu.tamu.app.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.tamu.app.enums.AppRole;
import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

public class MockData {

	protected static ApiResponse response;

	protected static Credentials credentials = new Credentials();

	protected static Credentials testCredentials;

	protected static AppUser TEST_USER1 = new AppUser(123456789l, "Jane", "Daniel", AppRole.ROLE_ADMIN.toString());
	protected static AppUser TEST_USER2 = new AppUser(234567891l, "Jack", "Daniel", AppRole.ROLE_MANAGER.toString());
	protected static AppUser TEST_USER3 = new AppUser(345678912l, "Jill", "Daniel", AppRole.ROLE_USER.toString());
	protected static List<AppUser> mockAppUserList = new ArrayList<AppUser>(Arrays.asList(new AppUser[] {TEST_USER1 , TEST_USER2 , TEST_USER3}));

	static {
		TEST_USER1.setId(1l);
		TEST_USER2.setId(2l);
		TEST_USER3.setId(3l);
		//credentials
		credentials.setAffiliation("TAMU_LIB");
		credentials.setFirstName(TEST_USER1.getFirstName());
		credentials.setLastName(TEST_USER1.getLastName());
		credentials.setEmail("aggieJane@tamu.edu");
		credentials.setNetid("aggieJane");
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

	public AppUser findUserByUin(Long uin) {
		for(AppUser user: mockAppUserList) {
			if(user.getUin().equals(uin))
			return user;
		}
		return null;
	}

	public AppUser createAppUser(Long uin, String firstName, String lastName, String role) {
		return new AppUser(uin, firstName, lastName, role);
	}

	protected static Map<String, String> aggieJackToken;

	//Project
	protected static Project TEST_PROJECT1 = new Project("Project Name 1");
	protected static Project TEST_PROJECT2 = new Project("Project Name 2");
	protected static Project TEST_PROJECT3 = new Project("Project Name 3");

	protected static Document document1 = new Document(TEST_PROJECT1, "Document2 Name ", "Document2 txtUri ", "Document2 pdfUri", "Document2 txtPath", "Document2 pdfPath ", "Published");
	protected static Document document2 = new Document(TEST_PROJECT1, "Document2 Name ", "Document2 txtUri ", "Document2 pdfUri", "Document2 txtPath", "Document2 pdfPath ", "Accepted");
	protected static Document document3 = new Document(TEST_PROJECT1, "Document2 Name ", "Document2 txtUri ", "Document2 pdfUri", "Document2 txtPath", "Document2 pdfPath ", "Pending");
	protected static Document document4 = new Document(TEST_PROJECT1, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");
	
	protected static Set<Document> mockDocumentSet = new HashSet<Document>(Arrays.asList(new Document[] {document1 , document2 , document3}));//new ArrayList<Document>();
	//FieldProfile(Project project, String gloss, Boolean repeatable, Boolean readOnly, Boolean hidden, Boolean required, InputType inputType, String defaultValue)
	//MetadataFieldGroup(Document document, MetadataFieldLabel label)
	//MetadataFieldLabel(String name, FieldProfile profile)
	protected static FieldProfile fieldProfile1 = new FieldProfile(TEST_PROJECT1, "Title", false, true, false, true, InputType.TEXT, "FieldProfile defaultValue");
	
	protected static MetadataFieldLabel metadataFieldLabel1 = new MetadataFieldLabel("MetadataFieldLabel Name", fieldProfile1);
	
	protected static MetadataFieldGroup metadataFieldGroup1 = new MetadataFieldGroup(new Document(), metadataFieldLabel1);
	protected static MetadataFieldGroup metadataFieldGroup2 = new MetadataFieldGroup(new Document(), metadataFieldLabel1);
	protected static MetadataFieldGroup metadataFieldGroup3 = new MetadataFieldGroup(new Document(), metadataFieldLabel1);
	
	protected static List<MetadataFieldGroup>mockMetadataFieldGroupList  = Arrays.asList(new MetadataFieldGroup[] {metadataFieldGroup1 , metadataFieldGroup2 , metadataFieldGroup3});
	static {
		TEST_PROJECT1.setId(1l); TEST_PROJECT1.setDocuments(mockDocumentSet);//TEST_PROJECT1.addDocument(document1);TEST_PROJECT1.addDocument(document2);
		TEST_PROJECT2.setId(2l);
		TEST_PROJECT3.setId(3l);
		//document
		document1.addField(metadataFieldGroup1);
	}

	protected static List<Project> mockProjectList = new ArrayList<Project>(Arrays.asList(new Project[] {TEST_PROJECT1 , TEST_PROJECT2 , TEST_PROJECT3}));

	public Project saveProject(Project modifiedProject) {
		for(Project project : mockProjectList) {
			if(project.getId().equals(modifiedProject.getId())) {
				project.setName(modifiedProject.getName());
				project.setIsLocked(false);
				return project;
			}
		}
		return null;
	}

	public Project findProjectByName(String projectname) {
		for(Project project : mockProjectList) {
			if(project.getName().equals(projectname)) {
				return project;
			}
		}
		return null;
	}

	//Metadata headers 
	protected static String[] mockMetadataHeaders = {"BUNDLE:ORIGINAL","dc.contributor.advisor","dc.contributor.committeeMember","dc.creator","dc.date.created",
	                                                 "dc.date.issued","dc.description","dc.description.abstract","dc.format.digitalOrigin","dc.format.medium",
	                                                 "dc.language.iso","dc.publisher","dc.rights","dc.subject","dc.subject.lcsh","dc.title","dc.type","dc.type.genre",
	                                                 "dc.type.material","thesis.degree.grantor","thesis.degree.level","thesis.degree.name"};
	
	protected static String[] mockDefaultMetadataHeaders = {"BUNDLE:ORIGINAL","dc.description","dc.title"};

	protected static Map<String, Object> cvMap = new HashMap<String, Object>();

}
