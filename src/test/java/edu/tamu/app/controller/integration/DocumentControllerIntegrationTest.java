package edu.tamu.app.controller.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ResourceRepo;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebServerInit.class)
public class DocumentControllerIntegrationTest {

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private MetadataFieldGroupRepo mfgRepo;

    @Autowired
    private MetadataFieldValueRepo mfvRepo;

    @Autowired
    private MetadataFieldLabelRepo mflRepo;

    @Autowired
    private FieldProfileRepo fpRepo;

    private Document mockDocument;
    @SuppressWarnings("unused")
    private Resource mockResource1;
    @SuppressWarnings("unused")
    private Resource mockResource2;
    private Project mockProject;
    private MetadataFieldGroup mfg;
    private MetadataFieldValue mfv1;
    private MetadataFieldValue mfv2;
    private FieldProfile mfp;

    @Before
    public void setUp() {
        // In order to realistically delete the Document, it needs to have some Resources and metadata on it, and be part of a Project.
        mockProject = projectRepo.create("Test Project", IngestType.STANDARD, false);
        mockDocument = new Document(mockProject, "testDocument", "documentPath", "Unassigned");
        mfp = fpRepo.create(mockProject, "Subject", true, false, false, true, InputType.TEXT, "Default");
        mockProject.addProfile(mfp);
        mockDocument = documentRepo.create(mockProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());

        mockResource1 = resourceRepo.create(mockDocument, "testResource1", "resourcePath1", "mime/type1");
        mockResource2 = resourceRepo.create(mockDocument, "testResource2", "resourcePath2", "mime/type2");

        MetadataFieldLabel testLabel = mflRepo.create("Subject Keyword", mfp);
        mfg = mfgRepo.create(mockDocument, testLabel);
        mfv1 = mfvRepo.create("Subject One", mfg);
        mfv2 = mfvRepo.create("Subject Two", mfg);
        mfg.addValue(mfv1);
        mfg.addValue(mfv2);

        mockDocument = documentRepo.findByProjectNameAndName(mockProject.getName(), mockDocument.getName());
        mockProject = projectRepo.findByName(mockProject.getName());
    }

    @Test
    public void testDelete() {
         //assertEquals("DocumentRepo is empty.", 1, documentRepo.count());
        
         // NOTE: this is not how controller integration tests should be
         // The context should start and setup, then a REST call should be made with appropriate JWT for authorization
        
         //TODO:  call document controller method remove()
        
//         assertEquals("Test Document was not removed.", 0, documentRepo.count());
//         assertEquals("Test Resources were not delete when document was removed.", 0, resourceRepo.count());
//         assertEquals("Test Document was not removed from Test Project when it was deleted", 0, mockProject.getDocuments().size());
//         assertEquals("Test MetadataFieldValues were not deleted from the Test Document when it was deleted.", 0, mfvRepo.findAll().size());
    }

    @After
    public void tearDown() throws Exception {
        mfvRepo.deleteAll();
        mflRepo.deleteAll();
        mfgRepo.deleteAll();
        fpRepo.deleteAll();
        resourceRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
    }

}
