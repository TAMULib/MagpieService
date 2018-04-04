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
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.InputType;
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
import edu.tamu.app.model.repo.ResourceRepo;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebServerInit.class)
public class CsvUtilityTest {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;
    
    @Autowired
    private ResourceRepo resourceRepo;

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

    private CsvUtility csvUtility;

    static final String dateValue = "1962";
    static final String abstractValue = "The emergence of an idea champion is an essential ingredient in the process of strategic change. The idea champion provides energy to move the system, yet \"we know practically nothing about this activity\" (Daft & Bradshow, 1980: 55). The purpose of this research is to examine varieties of the championship process to discover effective paths to strategy innovation and emergent strategic leadership. The research pits two competing persuasion-based, process oriented methods of influencing change. According to credit building theory (Hollander, 1960), an individual must build credits by initial conformity to group opinion in order to successfully influence innovation later. According to conversion theory (Moscovici, 1980), an individual achieves influence by consistently and resolutely not conforming from the outset, thereby eroding group consensus. The research also pits two competing influence tactics, praise and criticism. 129 top managers from the local business community participated in roundtable discussions during which a confederate advocated a new and unpopular strategic direction. In the 2 x 2 experimental design, the advocate's method (credit building or conversion) and tactic (praise or criticism) varied. The fates of an idea and its advocate are not necessarily intertwined. Consequently, managers' behavioral and resource allocation intentions toward the new strategy and managers' appraisals of the advocate's leadership abilities constituted major classes of dependent variables. Measurements were taken immediately after the discussion and several weeks later. Employing each manager's initial opinion toward the new strategy as the covariate, repeated measures multivariate analyses were conducted. The results were surprising. No main effect differences were found. But, the results disclosed a significant and durable interaction effect. The combination of credit building with criticism and conversion with praise were the effective paths to strategy innovation and emergent strategic leadership. Attempts to identify personal characteristics of early subscribers to the new strategy were fruitless. Where significant outcomes are wrought, it is (1) agreers who criticize or (2) disagreers who praise who forge them. The findings illuminate the construction of a new model of lone wolf influence emphasizing simultaneous give and take as the key to effective strategic championship.";
    static final String descValue = "12 pages long";
    static final String descValue2 = "additional description";

    @Test
    public void testGenerateOneArchiveMaticaCSV() throws IOException {
        testProject = projectRepo.create("testProject", IngestType.STANDARD, false);
        mockDocument = documentRepo.create(testProject, "testDocument", "documentPath", "Unassigned");

        FieldProfile createdProfile = projectFieldProfileRepo.create(testProject, "Date Created", false, false, false, false, InputType.TEXT, null);
        MetadataFieldLabel dateCreatedLabel = metadataFieldLabelRepo.create("dc.date.created", createdProfile);
        MetadataFieldGroup dateCreatedFieldGroup = metadataFieldGroupRepo.create(mockDocument, dateCreatedLabel);
        MetadataFieldValue dateCreatedValue = metadataFieldValueRepo.create(dateValue, dateCreatedFieldGroup);
        
        FieldProfile issueProfile = projectFieldProfileRepo.create(testProject, "Date Issued", false, false, false, false, InputType.TEXT, null);
        MetadataFieldLabel dateIssuedLabel = metadataFieldLabelRepo.create("dc.date.issued", issueProfile);
        MetadataFieldGroup dateIssuedFieldGroup = metadataFieldGroupRepo.create(mockDocument, dateIssuedLabel);
        MetadataFieldValue dateIssuedValue = metadataFieldValueRepo.create(dateValue, dateCreatedFieldGroup);

        FieldProfile descriptionAbstractProfile = projectFieldProfileRepo.create(testProject, "Abstract", false, false, false, false, InputType.TEXT, null);
        MetadataFieldLabel descriptionAbstractLabel = metadataFieldLabelRepo.create("dc.description.abstract", descriptionAbstractProfile);
        MetadataFieldGroup descriptionAbstractFieldGroup = metadataFieldGroupRepo.create(mockDocument, descriptionAbstractLabel);
        MetadataFieldValue descriptionAbstractValue = metadataFieldValueRepo.create(abstractValue, descriptionAbstractFieldGroup);

        FieldProfile descriptionProfile1 = projectFieldProfileRepo.create(testProject, "Description", false, false, false, false, InputType.TEXT, null);
        MetadataFieldLabel descriptionLabel1 = metadataFieldLabelRepo.create("dc.description", descriptionProfile1);
        MetadataFieldGroup descriptionFieldGroup = metadataFieldGroupRepo.create(mockDocument, descriptionLabel1);
        MetadataFieldValue descriptionValue1 = metadataFieldValueRepo.create(descValue, descriptionFieldGroup);
        MetadataFieldValue descriptionValue2 = metadataFieldValueRepo.create(descValue2, descriptionFieldGroup);

        descriptionFieldGroup.addValue(descriptionValue1);
        descriptionFieldGroup.addValue(descriptionValue2);

        descriptionAbstractFieldGroup.addValue(descriptionAbstractValue);
        dateCreatedFieldGroup.addValue(dateCreatedValue);
        dateIssuedFieldGroup.addValue(dateIssuedValue);

        mockDocument.addField(dateCreatedFieldGroup);
        mockDocument.addField(dateIssuedFieldGroup);
        mockDocument.addField(descriptionAbstractFieldGroup);
        mockDocument.addField(descriptionFieldGroup);

        csvUtility = new CsvUtility();

        List<List<String>> csvContents = csvUtility.generateOneArchiveMaticaCSV(mockDocument, "temp");

        assertEquals("There were the wrong number of column headings in the CSV.", 5, csvContents.get(0).size());
        assertTrue("The column headings were missing the parts heading.", csvContents.get(0).contains("parts"));
        assertTrue("The column headings were missing the description heading.", csvContents.get(0).contains("dc.description"));
        assertTrue("The column headings were missing the date heading.", csvContents.get(0).contains("dc.date"));

        int i = 0;
        for (String columnHeading : csvContents.get(0)) {

            switch (columnHeading) {
            case "parts":
                assertEquals("The parts column was incorrect.", "objects/testDocument", csvContents.get(1).get(i));
                break;
            case "description":
                assertTrue("One of the description columns was incorrect", (abstractValue.equals(csvContents.get(1).get(i))) || (descValue.equals(csvContents.get(1).get(i))));
                break;
            case "date":
                assertEquals("The date column was incorrect", dateValue, csvContents.get(1).get(i));
                break;
            }
            i++;
        }

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
        resourceRepo.deleteAll();
        documentRepo.deleteAll();
        projectRepo.deleteAll();
    }

}
