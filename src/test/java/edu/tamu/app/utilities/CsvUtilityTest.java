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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.tdl.vireo.annotations.Order;
import org.tdl.vireo.runner.OrderedRunner;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectProfile;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

@WebAppConfiguration
@ActiveProfiles({ "test" })
@RunWith(OrderedRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
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
	private ProjectProfileRepo projectFieldProfileRepo;
	
	private Project testProject;
	
	private Document mockDocument;

	@Test
	@Order(1)
	public void testGenerateOneArchiveMaticaCSV() throws IOException {
		testProject = projectRepo.create("testProject");
		mockDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");
		
		ProjectProfile profile = projectFieldProfileRepo.create(testProject, "Date Created", false, false, false, false, InputType.TEXT, null);
		MetadataFieldLabel dateCreatedLabel = metadataFieldLabelRepo.create("dc.date.created", profile);
		MetadataFieldGroup dateCreatedFields = metadataFieldGroupRepo.create(mockDocument, dateCreatedLabel);
		MetadataFieldValue dateCreatedValue = metadataFieldValueRepo.create("1962", dateCreatedFields);

		dateCreatedFields.addValue(dateCreatedValue);

		mockDocument.addField(dateCreatedFields);
		mockDocument.setPublishedUriString("http://hdl.handle.net/1969.1/12345");
		
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

}
