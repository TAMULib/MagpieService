package edu.tamu.app.controller.integration;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ResourceRepo;

public class DocumentControllerIntegrationTestTest {
    
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
    private FieldProfileRepo fpRepo;
    
    private Document mockDocument;
    private Resource mockResource1;
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
        mfp = fpRepo.create(mockProject, "Subject", true, false, false, true, InputType.TEXT, "Default");
        mockProject.addProfile(mfp);
        mockDocument = documentRepo.create(mockProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());
        
        mockResource1 = resourceRepo.create(mockDocument, "testResource1", "resourcePath1", "mime/type1");
        mockResource2 = resourceRepo.create(mockDocument, "testResource2", "resourcePath2", "mime/type2");
        
        mfg = mfgRepo.create(mockDocument, new MetadataFieldLabel("Subject Keyword", mfp));
        mfv1 = mfvRepo.create("Subject One", mfg);
        mfv2 = mfvRepo.create("Subject Two", mfg);
        mfg.addValue(mfv1);
        mfg.addValue(mfv2);
        
        
        
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDelete() {
        
        assertEquals("DocumentRepo is empty.", 1, documentRepo.count());
        documentRepo.delete(mockDocument);
        assertEquals("Test Document was not removed.", 0, documentRepo.count());
        assertEquals("Test Resources were not delete when document was removed.", 0, resourceRepo.count());
        assertEquals("Test Document was not removed from Test Project when it was deleted", 0, mockProject.getDocuments().size());
        assertEquals("Test MetadataFieldValues were not deleted from the Test Document when it was deleted.", 0, mfvRepo.findAll().size());
        

    }

}
