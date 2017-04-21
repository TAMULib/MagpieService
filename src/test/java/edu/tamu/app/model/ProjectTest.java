package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProjectTest extends AbstractModelTest {

    @Test
    public void testSaveProject() {
        assertProject = projectRepo.create("testProject");
        assertEquals("Test Project was not created.", 1, projectRepo.count());
        assertEquals("Expected Test Project was not created.", "testProject", assertProject.getName());
    }

    @Test
    public void testDuplicateProject() {
        projectRepo.create("testProject");
        projectRepo.create("testProject");
        assertEquals("Duplicate Test Project was created.", 1, projectRepo.count());
    }

    @Test
    public void testFindProject() {
        projectRepo.create("testProject");
        assertEquals("Test Project was not created.", 1, projectRepo.count());

        assertProject = projectRepo.findByName("testProject");

        assertEquals("Test Project was not found.", "testProject", assertProject.getName());
    }

    @Test
    public void testDeleteProject() {
        assertProject = projectRepo.create("testProject");
        assertEquals("Test Project was not created.", 1, projectRepo.count());
        projectRepo.delete(assertProject);
        assertEquals("Test Project was not deleted.", 0, projectRepo.count());
    }

    @Test
    public void testCascadeOnDeleteProject() {

        testProject = projectRepo.create("testProject");
        assertEquals("Test Project was not created.", 1, projectRepo.count());

        assertEquals("DocumentRepo is not empty.", 0, documentRepo.count());
        testDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "documentPath", "Unassigned");

        assertEquals("Test Document was not created.", 1, documentRepo.count());

        testProject.addDocument(testDocument);

        testProject = projectRepo.save(testProject);

        assertEquals("Test Project does not have any documents.", 1, testProject.getDocuments().size());

        projectRepo.delete(testProject);

        assertEquals("Test Document was not deleted.", 0, documentRepo.count());

    }

}
