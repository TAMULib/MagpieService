package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

public class ProjectFieldProfileTest extends AbstractModelTest {

    @BeforeEach
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals(0, projectFieldProfileRepo.count(), "ProjectFieldProfileRepo is not empty.");
    }

    @Test
    public void testSaveProjectFieldProfile() {
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals(1, projectFieldProfileRepo.count(), "Test ProjectFieldProfile was not created.");

        assertEquals(testProject.getName(), testProfile.getProject().getName(), "Test ProjectFieldProfile with expected project was not created.");

        testProfile.setProject(testProject);
        projectFieldProfileRepo.save(testProfile);
        testProfile = projectFieldProfileRepo.findByProjectAndGloss(testProject, "testGloss");
        assertEquals(testProfile.getProject().getName(), testProject.getName(), " The testProfile project was not set ");
    }

    @Test
    public void testDuplicateProjectFieldProfile() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
            projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        });
    }

    @Test
    public void testFindProjectFieldProfile() {
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals(1, projectFieldProfileRepo.count(), "Test ProjectFieldProfile was not created.");
        FieldProfile assertProfile = projectFieldProfileRepo.findByProjectAndGloss(testProject, "testGloss");
        assertEquals(testProfile.getProject().getName(), assertProfile.getProject().getName(), "Test ProjectFieldProfile with expected project was not found.");
    }

    @Test
    @Transactional
    public void testDeleteProjectFieldProfile() {
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals(1, projectFieldProfileRepo.count(), "Test ProjectFieldProfile was not created.");
        projectFieldProfileRepo.delete(testProfile);
        assertEquals(0, projectFieldProfileRepo.count(), "Test ProjectFieldProfile was not deleted.");
    }

    @Test
    public void testSettersFieldProfile() {

        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXTAREA, "default");
        Set<MetadataFieldLabel> labels = new HashSet<MetadataFieldLabel>();
        labels.add(metadataFieldLabelRepo.create("Field Label Name 1", testProfile));
        labels.add(metadataFieldLabelRepo.create("Field Label Name 2", testProfile));

        testProfile.setGloss("test Profile gloss");
        testProfile.setRepeatable(true);
        testProfile.setReadOnly(true);
        testProfile.setHidden(true);
        testProfile.setRequired(true);
        testProfile.setInputType(InputType.TEXT);
        testProfile.setDefaultValue("This is a default Value");
        testProfile.setLabels(labels);

        testProfile = projectFieldProfileRepo.save(testProfile);

        assertTrue(testProfile.isRepeatable());
        assertTrue(testProfile.isReadOnly());
        assertTrue(testProfile.isHidden());
        assertTrue(testProfile.isRequired());
        assertEquals("test Profile gloss", testProfile.getGloss(), "The field profile gloss is incorrect");
        assertEquals(InputType.TEXT, testProfile.getInputType(), "The field profile input type is incorrect");
        assertEquals("This is a default Value", testProfile.getDefaultValue(), "The field profile default value is incorrect");
        assertEquals(labels.size(), testProfile.getLabels().size(), "The field profile does not have metadata field labels");

        testLabel = metadataFieldLabelRepo.create("Field Label Name 3", testProfile);
        testProfile.addLabel(testLabel);
        testProfile = projectFieldProfileRepo.save(testProfile);
        testProfile.removeLabel(testLabel);
        testProfile = projectFieldProfileRepo.save(testProfile);
        assertEquals(labels.size(), testProfile.getLabels().size(), "The third metadata field label was not removed");

    }

}
