package edu.tamu.app.model.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.tamu.app.model.AbstractModelTest;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.PublishedLocation;
import edu.tamu.app.model.ServiceType;

public class ProjectModelIntegrationTest extends AbstractModelTest {

    private ProjectRepository projectRepository;

    @BeforeEach
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

        assertNotNull(testProject.getRepositories().get(0), "Test Project did not have the expected repository!");

        assertEquals("Test Repository", testProject.getRepositories().get(0).getName(), "Test Project did not have the expected repository name!");
        assertEquals(ServiceType.FEDORA_SPOTLIGHT, testProject.getRepositories().get(0).getType(), "Test Project did not have the expected repository service type!");
    }

    @Test
    public void testProjectDocument() {
        testProjectRepository();

        testDocument = documentRepo.create(testProject, mockDocument.getName(), mockDocument.getPath(), mockDocument.getStatus());

        testProject = projectRepo.findByName("testProject");

        assertNotNull(testProject.getDocuments().get(0), "Test Project did not have the expected document!");

        assertEquals(mockDocument.getName(), testProject.getDocuments().get(0).getName(), "Test Project did not have the expected document name!");
        assertEquals(mockDocument.getPath(), testProject.getDocuments().get(0).getPath(), "Test Project did not have the expected document path!");
        assertEquals(mockDocument.getStatus(), testProject.getDocuments().get(0).getStatus(), "Test Project did not have the expected document status!");
    }

    @Test
    public void testDocumentPublishedLocation() {
        testProjectDocument();

        PublishedLocation publishedLocation = new PublishedLocation(testProject.getRepositories().get(0), "http://localhost:9000/fcrepo/rest/collection/resource/test.jpg");

        testDocument.addPublishedLocation(publishedLocation);

        testDocument = documentRepo.save(testDocument);

        assertNotNull(testDocument.getPublishedLocations().get(0), "Test Document did not have the expected published location!");

        assertEquals(projectRepository.getName(), testDocument.getPublishedLocations().get(0).getRepository().getName(), "Test Document did not have the expected published location repository name!");
        assertEquals(projectRepository.getType(), testDocument.getPublishedLocations().get(0).getRepository().getType(), "Test Document did not have the expected published location repository service type!");
        assertEquals(publishedLocation.getUrl(), testDocument.getPublishedLocations().get(0).getUrl(), "Test Document did not have the expected published location url!");
    }

}
