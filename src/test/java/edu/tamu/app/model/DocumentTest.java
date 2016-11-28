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

import edu.tamu.app.WebServerInit;
import edu.tamu.app.annotations.Order;
import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.runner.OrderedRunner;

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
    private FieldProfileRepo projectProfileRepo;

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
        Assert.assertEquals("Test Document already exists.", null, documentRepo.findByProjectNameAndName(testProject.getName(), "testFile"));
        documentRepo.create(testProject, mockDocument.getName(), mockDocument.getTxtUri(), mockDocument.getTxtPath(), mockDocument.getPdfUri(), mockDocument.getPdfPath(), mockDocument.getStatus());
        Document testDocument = documentRepo.findByProjectNameAndName(mockDocument.getProject().getName(), mockDocument.getName());
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

        Assert.assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectProfileRepo.count());
        FieldProfile testProfile = projectProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        Assert.assertEquals("Test ProjectFieldProfile was not created.", 1, projectProfileRepo.count());

        Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
        MetadataFieldLabel testLabel = metadataFieldLabelRepo.create("testLabel", testProfile);
        Assert.assertEquals("Test MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());

        Assert.assertEquals("MetadataFieldRepo is not empty.", 0, metadataFieldGroupRepo.count());
        MetadataFieldGroup testField = metadataFieldGroupRepo.create(testDocument, testLabel);
        Assert.assertEquals("Test MetadataField was not created.", 1, metadataFieldGroupRepo.count());

        Assert.assertEquals("MetadataFieldValue repository is not empty.", 0, metadataFieldValueRepo.count());
        MetadataFieldValue fieldValue = metadataFieldValueRepo.create("test", testField);
        Assert.assertEquals("Test MetadataFieldValue was not created.", 1, metadataFieldValueRepo.count());
        
        testField.addValue(fieldValue);
        
        testDocument.addField(testField);

        testDocument = documentRepo.save(testDocument);
        
        
        Assert.assertEquals("Test Document was not created.", 1, documentRepo.count());
        
        Assert.assertEquals("Test Document had an incorrect number of fields.", 1, testDocument.getFields().size());
        
        Assert.assertEquals("Test Document's field had an incorrect number of values.", 1, testDocument.getFields().get(0).getValues().size());
        

        documentRepo.delete(testDocument);

        Assert.assertEquals("Test Document was not deleted.", 0, documentRepo.count());

        Assert.assertEquals("Test MetadataFieldLabel was deleted.", 1, metadataFieldLabelRepo.count());

        Assert.assertEquals("Test ProjectFieldProfile was deleted.", 1, projectProfileRepo.count());

        Assert.assertEquals("Test MetadataField was not deleted.", 0, metadataFieldGroupRepo.count());

        Assert.assertEquals("Test MetadataFieldValue was not deleted.", 0, metadataFieldValueRepo.count());
    }

    @After
    public void cleanUp() {
        projectProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
    }

}
