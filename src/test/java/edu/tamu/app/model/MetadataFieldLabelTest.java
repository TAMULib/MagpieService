package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

public class MetadataFieldLabelTest extends AbstractModelTest {

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
    }

    @Test
    public void testCreateMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
    }

    @Test
    public void testDuplicateMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        metadataFieldLabelRepo.create("test", testProfile);
        assertEquals("MetadataFieldLabel has duplicate.", 1, metadataFieldLabelRepo.count());
    }

    @Test
    public void testFindMetadataFieldLabel() {
        MetadataFieldLabel assertLabel = metadataFieldLabelRepo.create("test", testProfile);
        assertEquals("MetadataFieldLabel was not found.", assertLabel.getName(), metadataFieldLabelRepo.findByNameAndProfile("test", testProfile).getName());
    }

    @Test
    @Transactional
    public void testDeleteMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
        MetadataFieldLabel label = metadataFieldLabelRepo.findByNameAndProfile("test", testProfile);
        assertNotNull("Metadatafield was not retrieved!", label);
        metadataFieldLabelRepo.delete(label);
        assertEquals("MetadataFieldLabel was not deleted.", 0, metadataFieldLabelRepo.count());
    }

}
