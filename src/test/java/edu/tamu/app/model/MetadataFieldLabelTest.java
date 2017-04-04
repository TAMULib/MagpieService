package edu.tamu.app.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.tdl.vireo.annotations.Order;

import edu.tamu.app.enums.InputType;

public class MetadataFieldLabelTest extends AbstractClass {

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject");
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
    }

    @Test
    @Order(1)
    public void testCreateMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
    }

    @Test
    @Order(2)
    public void testDuplicateMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        metadataFieldLabelRepo.create("test", testProfile);
        assertEquals("MetadataFieldLabel has duplicate.", 1, metadataFieldLabelRepo.count());
    }

    @Test
    @Order(3)
    public void testFindMetadataFieldLabel() {
        MetadataFieldLabel assertLabel = metadataFieldLabelRepo.create("test", testProfile);
        assertEquals("MetadataFieldLabel was not found.", assertLabel.getName(), metadataFieldLabelRepo.findByName("test").getName());
    }

    @Test
    @Order(4)
    public void testDeleteMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
        metadataFieldLabelRepo.delete(metadataFieldLabelRepo.findByName("test"));
        assertEquals("MetadataFieldLabel was not deleted.", 0, metadataFieldLabelRepo.count());
    }

}
