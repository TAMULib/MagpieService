package edu.tamu.app.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebServerInit.class)
public class CsvUtilityTest {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private CsvUtility csvUtility;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Autowired
    private FieldProfileRepo projectFieldProfileRepo;

    private Project testProject;

    private Document mockDocument;

    @Test
    public void testGenerateOneArchiveMaticaCSV() throws IOException {
        testProject = projectRepo.create("testProject");
        mockDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "documentPath", "Unassigned");

        FieldProfile profile = projectFieldProfileRepo.create(testProject, "Date Created", false, false, false, false, InputType.TEXT, null);
        MetadataFieldLabel dateCreatedLabel = metadataFieldLabelRepo.create("dc.date.created", profile);
        MetadataFieldGroup dateCreatedFieldGroup = metadataFieldGroupRepo.create(mockDocument, dateCreatedLabel);
        MetadataFieldValue dateCreatedValue = metadataFieldValueRepo.create("1962", dateCreatedFieldGroup);

        dateCreatedFieldGroup.addValue(dateCreatedValue);

        mockDocument.addField(dateCreatedFieldGroup);
        // mockDocument.setPublishedUriString("http://hdl.handle.net/1969.1/12345");

        List<List<String>> csvContents = csvUtility.generateOneArchiveMaticaCSV(mockDocument, "temp");

        assertEquals("The parts column was incorrect.", csvContents.get(1).get(0), "objects/testDocument");
        assertEquals("The dc.date.created was incorrect.", csvContents.get(1).get(1), "1962");

        Path path = Paths.get("temp");

        assertTrue("temp path is not a directory", Files.isDirectory(path));

        Files.walk(path).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                System.out.println(filePath);
            }
        });

        // TODO: assert values of csv file are as expected

        FileUtils.deleteDirectory(new File("temp"));
    }

    @After
    public void cleanUp() {
        metadataFieldValueRepo.deleteAll();
        metadataFieldLabelRepo.deleteAll();
        metadataFieldGroupRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
    }

}
