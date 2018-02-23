package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProjectTest extends AbstractModelTest {

    @Test
    public void testSaveProject() {
        assertProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals("Test Project was not created.", 1, projectRepo.count());
        assertEquals("Expected Test Project was not created.", "testProject", assertProject.getName());
    }

    @Test
    public void testDuplicateProject() {
        projectRepo.create("testProject", IngestType.STANDARD, false);
        projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals("Duplicate Test Project was created.", 1, projectRepo.count());
    }

    @Test
    public void testFindProject() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals("Test Project was not created.", 1, projectRepo.count());
        testProject = projectRepo.findByName("testProject");
        assertEquals("Test Project was not found.", "testProject", testProject.getName());
    }

    @Test
    public void testDeleteProject() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals("Test Project was not created.", 1, projectRepo.count());
        projectRepo.delete(testProject);
        assertEquals("Test Project was not deleted.", 0, projectRepo.count());
    }

    @Test
    public void testCascadeOnDeleteProject() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals("Test Project was not created.", 1, projectRepo.count());

        assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
        testDocument = documentRepo.create(testProject, "testDocument", "documentPath", "Unassigned");

        assertEquals("Test Document was not created.", 1, documentRepo.count());

        testProject.addDocument(testDocument);

        testProject = projectRepo.save(testProject);

        assertEquals("Test Project does not have any documents.", 1, testProject.getDocuments().size());

        projectRepo.delete(testProject);

        assertEquals("Test Document was not deleted.", 0, documentRepo.count());
    }

}
