package edu.tamu.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.xml.sax.SAXException;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@ActiveProfiles("test")
@SpringBootTest(classes = WebServerInit.class)
public class DocumentFactoryTest {

    @Autowired
    private ProjectFactory projectFactory;

    @Autowired
    private DocumentFactory documentFactory;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Test
    public void testCreateDocument() throws IOException, SAXException, ParserConfigurationException {
        projectFactory.getOrCreateProject("default");
        documentFactory.createDocument(new File("default/default"));

        assertEquals(1, projectRepo.count(), "The project repo has the incorrect number of projects!");
        assertNotNull(projectRepo.findByName("default"), "The default project was not created!");

        assertEquals(1, documentRepo.count(), "The document repo has the incorrect number of documents!");
        assertNotNull(documentRepo.findByProjectNameAndName("default", "default"), "The default document was not created!");
    }

    @AfterEach
    public void cleanUp() {
        documentRepo.deleteAll();
        projectRepo.deleteAll();
        fieldProfileRepo.deleteAll();
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
    }
}
