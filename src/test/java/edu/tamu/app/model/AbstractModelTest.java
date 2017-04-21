package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.runner.OrderedRunner;

@WebAppConfiguration
@ActiveProfiles({"test"})
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
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
    protected MetadataFieldGroupRepo metadataFieldGroupRepo;

	@Autowired
    protected MetadataFieldLabelRepo metadataFieldLabelRepo;

	@Autowired
    protected MetadataFieldValueRepo metadataFieldValueRepo;

	@Autowired
    protected FieldProfileRepo projectFieldProfileRepo;

	@Before
	public void setUp() {
        aggieJackToken = new HashMap<>();
        aggieJackToken.put("lastName", "Daniels");
        aggieJackToken.put("firstName", "Jack");
        aggieJackToken.put("netid", "aggiejack");
        aggieJackToken.put("uin", "123456789");
        aggieJackToken.put("exp", String.valueOf(timestamp));
        aggieJackToken.put("email", "aggiejack@tamu.edu");

        assertEquals("User repository is not empty.", 0, userRepo.findAll().size());
	    assertEquals("ControlledVocabularyRepo is not empty.", 0, controlledVocabularyRepo.count());

	}

	@After
	public void cleanUp() {
		controlledVocabularyRepo.deleteAll();
		documentRepo.deleteAll();
		metadataFieldGroupRepo.deleteAll();
		metadataFieldLabelRepo.deleteAll();
		metadataFieldValueRepo.deleteAll();
		projectFieldProfileRepo.deleteAll();
		projectRepo.deleteAll();
		userRepo.deleteAll();
	}
}