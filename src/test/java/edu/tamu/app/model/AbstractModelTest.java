package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ResourceRepo;

@ActiveProfiles("test")
@SpringBootTest(classes = WebServerInit.class)
public abstract class AbstractModelTest extends MockData {

    @Autowired
    protected AppUserRepo userRepo;

    @Autowired
    protected ControlledVocabularyRepo controlledVocabularyRepo;

    @Autowired
    protected ProjectRepo projectRepo;

    @Autowired
    protected DocumentRepo documentRepo;

    @Autowired
    protected ResourceRepo resourceRepo;

    @Autowired
    protected MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    protected MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    protected MetadataFieldValueRepo metadataFieldValueRepo;

    @Autowired
    protected FieldProfileRepo projectFieldProfileRepo;

    @BeforeEach
    public void setUp() {
        aggieJackToken = new HashMap<String, Object>();
        aggieJackToken.put("lastName", "Daniels");
        aggieJackToken.put("firstName", "Jack");
        aggieJackToken.put("netid", "aggiejack");
        aggieJackToken.put("uin", "123456789");
        aggieJackToken.put("exp", String.valueOf(timestamp));
        aggieJackToken.put("email", "aggiejack@tamu.edu");

        assertEquals(0, userRepo.findAll().size(), "User repository is not empty.");
        assertEquals(0, controlledVocabularyRepo.count(), "ControlledVocabularyRepo is not empty.");
    }

    @AfterEach
    public void cleanUp() {
        controlledVocabularyRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        projectFieldProfileRepo.deleteAll();
        resourceRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
        userRepo.deleteAll();
    }

}
