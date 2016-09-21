package edu.tamu.app.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectProfile;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@WebAppConfiguration
@ActiveProfiles({ "test" })
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
public class MetadataFieldLabelTest {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private ProjectProfileRepo projectFieldProfileRepo;

    private Project testProject;

    private ProjectProfile testProfile;

    @Before
    public void setUp() {
        testProject = projectRepo.create("testProject");
        testProfile = projectFieldProfileRepo.create(testProject, "testGloss", false, false, false, false, InputType.TEXT, "default");
        Assert.assertEquals("MetadataFieldLabelRepo is not empty.", 0, metadataFieldLabelRepo.count());
    }

    @Test
    @Order(1)
    public void testCreateMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        Assert.assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
    }

    @Test
    @Order(2)
    public void testDuplicateMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        metadataFieldLabelRepo.create("test", testProfile);
        Assert.assertEquals("MetadataFieldLabel has duplicate.", 1, metadataFieldLabelRepo.count());
    }

    @Test
    @Order(3)
    public void testFindMetadataFieldLabel() {
        MetadataFieldLabel assertLabel = metadataFieldLabelRepo.create("test", testProfile);
        Assert.assertEquals("MetadataFieldLabel was not found.", assertLabel.getName(), metadataFieldLabelRepo.findByName("test").getName());
    }

    @Test
    @Order(4)
    public void testDeleteMetadataFieldLabel() {
        metadataFieldLabelRepo.create("test", testProfile);
        Assert.assertEquals("MetadataFieldLabel was not created.", 1, metadataFieldLabelRepo.count());
        metadataFieldLabelRepo.delete(metadataFieldLabelRepo.findByName("test"));
        Assert.assertEquals("MetadataFieldLabel was not deleted.", 0, metadataFieldLabelRepo.count());
    }

    @After
    public void cleanUp() {
        metadataFieldLabelRepo.deleteAll();
        projectFieldProfileRepo.deleteAll();
        projectRepo.deleteAll();
    }

}
