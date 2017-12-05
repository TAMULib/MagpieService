package edu.tamu.app.model;

import java.util.Date;
import java.util.Map;

public class MockData {

    protected String uin = "123456789";

    protected AppUser testUser1 = new AppUser(uin);

    protected AppUser testUser2 = new AppUser(uin);

    protected AppUser testUser3 = new AppUser("123456789", "Jane", "Daniel", "ROLE_ADMIN");

    protected ControlledVocabulary testControlledVocabulary;

    protected Document testDocument, mockDocument;
    
    protected Resource mockResource1, mockResource2;

    protected FieldProfile testProfile;

    protected MetadataFieldGroup testFieldGroup;

    protected MetadataFieldLabel testLabel;

    protected MetadataFieldValue testValue;

    protected Project testProject, assertProject;
    
    protected Map<String, Object> aggieJackToken;

    protected long timestamp = new Date().getTime() + (5 * 60 * 1000);

}
