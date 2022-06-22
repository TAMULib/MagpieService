package edu.tamu.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

public class MetadataFieldLabelTest extends AbstractModelTest {

    @BeforeEach
    public void setUp() {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        assertEquals(0, metadataFieldLabelRepo.count(), "MetadataFieldLabelRepo is not empty.");
    }

    @Test
    public void testCreateMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        assertEquals(1, metadataFieldLabelRepo.count(), "MetadataFieldLabel was not created.");
    }

    @Test
    public void testDuplicateMetadataFieldLabel() {
        assertThrows(DataIntegrityViolationException.class, () -> {
            metadataFieldLabelRepo.create("test", testProfile);
            metadataFieldLabelRepo.create("test", testProfile);
        });
    }

    @Test
    public void testFindMetadataFieldLabel() {
        MetadataFieldLabel assertLabel = metadataFieldLabelRepo.create("test", testProfile);
        assertEquals(assertLabel.getName(), metadataFieldLabelRepo.findByNameAndProfile("test", testProfile).getName(), "MetadataFieldLabel was not found.");
    }

    @Test
    @Transactional
    public void testDeleteMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        assertEquals(1, metadataFieldLabelRepo.count(), "MetadataFieldLabel was not created.");
        MetadataFieldLabel label = metadataFieldLabelRepo.findByNameAndProfile("test", testProfile);
        assertNotNull(label, "Metadatafield was not retrieved!");
        metadataFieldLabelRepo.delete(label);
        assertEquals(0, metadataFieldLabelRepo.count(), "MetadataFieldLabel was not deleted.");
    }

}
