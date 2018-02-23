package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

public class ProjectFieldProfileTest extends AbstractModelTest {

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        assertEquals("ProjectFieldProfileRepo is not empty.", 0, projectFieldProfileRepo.count());
    }

    @Test
    public void testSaveProjectFieldProfile() {
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());

        assertEquals("Test ProjectFieldProfile with expected project was not created.", testProject.getName(), testProfile.getProject().getName());

        testProfile.setProject(testProject);
        projectFieldProfileRepo.save(testProfile);
        testProfile = projectFieldProfileRepo.findByProjectAndGloss(testProject, "testGloss");
        assertEquals(" The testProfile project was not set ", testProfile.getProject().getName(), testProject.getName());
    }

    @Test
    public void testDuplicateProjectFieldProfile() {
        projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals("Test ProjectFieldProfile duplicate was created.", 1, projectFieldProfileRepo.count());
    }

    @Test
    public void testFindProjectFieldProfile() {
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
        FieldProfile assertProfile = projectFieldProfileRepo.findByProjectAndGloss(testProject, "testGloss");
        assertEquals("Test ProjectFieldProfile with expected project was not found.", testProfile.getProject().getName(), assertProfile.getProject().getName());
    }

    @Test
    @Transactional
    public void testDeleteProjectFieldProfile() {
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals("Test ProjectFieldProfile was not created.", 1, projectFieldProfileRepo.count());
        projectFieldProfileRepo.delete(testProfile);
        assertEquals("Test ProjectFieldProfile was not deleted.", 0, projectFieldProfileRepo.count());
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
        assertEquals("The field profile gloss is incorrect", "test Profile gloss", testProfile.getGloss());
        assertEquals("The field profile input type is incorrect", InputType.TEXT, testProfile.getInputType());
        assertEquals("The field profile default value is incorrect", "This is a default Value", testProfile.getDefaultValue());
        assertEquals("The field profile does not have metadata field labels", labels.size(), testProfile.getLabels().size());

        testLabel = metadataFieldLabelRepo.create("Field Label Name 3", testProfile);
        testProfile.addLabel(testLabel);
        testProfile = projectFieldProfileRepo.save(testProfile);
        testProfile.removeLabel(testLabel);
        testProfile = projectFieldProfileRepo.save(testProfile);
        assertEquals("The third metadata field label was not removed", labels.size(), testProfile.getLabels().size());

    }

}
