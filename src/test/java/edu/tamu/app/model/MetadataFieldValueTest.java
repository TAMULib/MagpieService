package edu.tamu.app.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@WebAppConfiguration
@ActiveProfiles({ "test" })
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class MetadataFieldValueTest {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Autowired
    private ProjectLabelProfileRepo projectFieldProfileRepo;

    @Autowired
    private ControlledVocabularyRepo controlledVocabularyRepo;

    private Project testProject;

    private Document testDocument;

    private MetadataFieldLabel testLabel;

    private ProjectLabelProfile testProfile;

    private MetadataFieldGroup testField;

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject");
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        testDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");
        testField = metadataFieldGroupRepo.create(testDocument, testLabel);
        Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
    }

    @Test
    @Order(1)
    public void testSaveMetadataFieldValue() {
        MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
        Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        Assert.assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());
    }

    @Test
    @Order(2)
    public void testSaveWithControlCharacterMetadataFieldValue() {
        MetadataFieldValue testValue = metadataFieldValueRepo.create("test\n\r\t\b\f", testField);
        Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        Assert.assertEquals("Expected Test MetadataFieldValue was not created.", "test", testValue.getValue());
    }

    @Test
    @Order(3)
    public void testFindMetadataFieldValue() {
        MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
        Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        testValue = metadataFieldValueRepo.findByValueAndField("test", testField);
        Assert.assertEquals("Test MetadataFieldValue was not found.", "test", testValue.getValue());
    }

    @Test
    @Order(4)
    public void testDeleteMetadataFieldValue() {
        MetadataFieldValue testValue = metadataFieldValueRepo.create("test", testField);
        Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        metadataFieldValueRepo.delete(testValue);
        Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
    }

    @Test
    @Order(5)
    public void testCascadeOnDeleteMetadataFieldValue() {
        ControlledVocabulary testControlledVocabulary = controlledVocabularyRepo.create("test");
        Assert.assertEquals("Test ControlledVocabulary was not created.", 1, controlledVocabularyRepo.count());

        Assert.assertEquals("MetadataFieldValueRepo is not empty.", 0, metadataFieldValueRepo.count());
        MetadataFieldValue testValue = metadataFieldValueRepo.create(testControlledVocabulary, testField);
        Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());

        Assert.assertEquals("Test MetadataFieldValue with ControlledVocabulary was not created.", testControlledVocabulary.getValue(), testValue.getValue());

        metadataFieldValueRepo.delete(testValue);

        Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());

        Assert.assertEquals("Test ControlledVocabulary was deleted.", 1, controlledVocabularyRepo.count());
    }

    @After
    public void cleanUp() {
        controlledVocabularyRepo.deleteAll();
        projectFieldProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
    }

}
