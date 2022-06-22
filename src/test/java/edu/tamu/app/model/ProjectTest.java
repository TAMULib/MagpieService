package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

public class ProjectTest extends AbstractModelTest {

    @Test
    public void testSaveProject() {
        assertProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals(1, projectRepo.count(), "Test Project was not created.");
        assertEquals("testProject", assertProject.getName(), "Expected Test Project was not created.");
    }

    @Test
    public void testDuplicateProject() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            projectRepo.create("testProject", IngestType.STANDARD, false);
            projectRepo.create("testProject", IngestType.STANDARD, false);
        });
    }

    @Test
    public void testFindProject() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals(1, projectRepo.count(), "Test Project was not created.");
        testProject = projectRepo.findByName("testProject");
        assertEquals("testProject", testProject.getName(), "Test Project was not found.");
    }

    @Test
    public void testDeleteProject() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals(1, projectRepo.count(), "Test Project was not created.");
        projectRepo.delete(testProject);
        assertEquals(0, projectRepo.count(), "Test Project was not deleted.");
    }

    @Test
    public void testCascadeOnDeleteProject() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals(1, projectRepo.count(), "Test Project was not created.");

        assertEquals(0, documentRepo.count(), "DocumentRepo is not empty.");
        testDocument = documentRepo.create(testProject, "testDocument", "documentPath", "Unassigned");

        assertEquals(1, documentRepo.count(), "Test Document was not created.");

        testProject.addDocument(testDocument);

        testProject = projectRepo.save(testProject);

        assertEquals(1, testProject.getDocuments().size(), "Test Project does not have any documents.");

        projectRepo.delete(testProject);

        assertEquals(0, documentRepo.count(), "Test Document was not deleted.");
    }

}
