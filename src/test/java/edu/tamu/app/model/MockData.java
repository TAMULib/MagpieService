package edu.tamu.app.model;

import java.util.Date;
import java.util.Map;

public class MockData {

	protected Long uin = 123456789L;
	
	protected AppUser testUser1 = new AppUser(uin);
    
	protected AppUser testUser2 = new AppUser(uin);
	
	protected ControlledVocabulary testControlledVocabulary;
	
	protected Document testDocument, mockDocument;
	
	protected FieldProfile testProfile;
	
	protected MetadataFieldGroup testField;
	
	protected MetadataFieldLabel testLabel;
	
	protected MetadataFieldValue testValue;
	
	protected Project testProject, assertProject;
	
	protected Map<String, String> aggieJackToken;
	
	protected long timestamp = new Date().getTime() + (5 * 60 * 1000);
}
