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
import edu.tamu.app.model.Document;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
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
public class DocumentTest {

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

    private Project testProject;

    private Document mockDocument;

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject");
        mockDocument = new Document(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");
        Assert.assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
    }

    @Test
    @Order(1)
    public void testCreateDocument() {
        Document testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
        Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());
        Assert.assertEquals("Expected Test Document was not created.", mockDocument.getName(), testDocument.getName());
    }

    @Test
    @Order(2)
    public void testFindDocument() {
        Assert.assertEquals("Test Document already exists.", null, documentRepo.findByName("testFile"));
        documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
        Document testDocument = documentRepo.findByName(mockDocument.getName());
        Assert.assertEquals("Test Document was not found.", mockDocument.getName(), testDocument.getName());
    }

    @Test
    @Order(3)
    public void testDeleteDocument() {
        Document testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
        Assert.assertEquals("DocumentRepo is empty.", 1, documentRepo.count());
        documentRepo.delete(testDocument);
        Assert.assertEquals("Test Document was not removed.", 0, documentRepo.count());
    }

    @Test
    @Order(4)
    public void testCascadeOnDeleteDocument() {
        Document testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
        Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());

        Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
        ProjectLabelProfile testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());

        Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
        MetadataFieldLabel testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        Assert.assertEquals("Test MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());

        Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldGroupRepo.count());
        MetadataFieldGroup testField = metadataFieldGroupRepo.create(testDocument, testLabel);
        Assert.assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());

        Assert.assertEquals("MetadataFieldValue repository is not empty.", 0, metadataFieldValueRepo.count());
        metadataFieldValueRepo.create("test", testField);
        Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());

        testDocument.addField(testField);

        testDocument = documentRepo.save(testDocument);

        documentRepo.delete(testDocument);

        Assert.assertEquals("Test Document was not deleted.", 0, documentRepo.count());

        Assert.assertEquals("Test MetadataFieldLabel was deleted.", 1, metadataFieldLabelRepo.count());

        Assert.assertEquals("Test ProjectFieldProfile was deleted.", 1, projectFieldProfileRepo.count());

        Assert.assertEquals("Test MetadataField was not deleted.", 0, metadataFieldGroupRepo.count());

        Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
    }

    @After
    public void cleanUp() {
        projectFieldProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
    }

}
