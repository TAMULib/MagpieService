package edu.tamu.app.model.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.app.model.AbstractModelTest;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.PublishedLocation;
import edu.tamu.app.model.ServiceType;

public class ProjectModelIntegrationTest extends AbstractModelTest {

    private ProjectRepository projectRepository;

    @Before
    public void setUp() {
        mockDocument = new Document(testProject, "testDocument", "documentPath", "Unassigned");
    }

    @Test
    public void testProjectRepository() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);

        projectRepository = new ProjectRepository();
        projectRepository.setName("Test Repository");
        projectRepository.setType(ServiceType.FEDORA_SPOTLIGHT);

        testProject.addRepository(projectRepository);

        testProject = projectRepo.save(testProject);

        assertNotNull("Test Project did not have the expected repository!", testProject.getRepositories().get(0));

        assertEquals("Test Project did not have the expected repository name!", "Test Repository", testProject.getRepositories().get(0).getName());
        assertEquals("Test Project did not have the expected repository service type!", ServiceType.FEDORA_SPOTLIGHT, testProject.getRepositories().get(0).getType());
    }

    @Test
    public void testProjectDocument() {
        testProjectRepository();

        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());

        testProject = projectRepo.findByName("testProject");

        assertNotNull("Test Project did not have the expected document!", testProject.getDocuments().get(0));

        assertEquals("Test Project did not have the expected document name!", mockDocument.getName(), testProject.getDocuments().get(0).getName());
        assertEquals("Test Project did not have the expected document path!", mockDocument.getPath(), testProject.getDocuments().get(0).getPath());
        assertEquals("Test Project did not have the expected document status!", mockDocument.getStatus(), testProject.getDocuments().get(0).getStatus());
    }

    @Test
    public void testDocumentPublishedLocation() {
        testProjectDocument();

        PublishedLocation publishedLocation = new PublishedLocation(testProject.getRepositories().get(0), "http://localhost:9000/fcrepo/rest/collection/resource/test.jpg");

        testDocument.addPublishedLocation(publishedLocation);

        testDocument = documentRepo.save(testDocument);

        assertNotNull("Test Document did not have the expected published location!", testDocument.getPublishedLocations().get(0));

        assertEquals("Test Document did not have the expected published location repository name!", projectRepository.getName(), testDocument.getPublishedLocations().get(0).getRepository().getName());
        assertEquals("Test Document did not have the expected published location repository service type!", projectRepository.getType(), testDocument.getPublishedLocations().get(0).getRepository().getType());
        assertEquals("Test Document did not have the expected published location url!", publishedLocation.getUrl(), testDocument.getPublishedLocations().get(0).getUrl());
    }

}
