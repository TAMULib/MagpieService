package edu.tamu.app.utilities;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.config.TestDataSourceConfiguration;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebServerInit.class)
@ContextConfiguration(classes = { TestDataSourceConfiguration.class })
public class CsvUtilityTest {

	@Autowired
	private ProjectRepo projectRepo;

	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private MetadataFieldGroupRepo metadataFieldGroupRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectFieldProfileRepo;
	
	private Project testProject;
	
	private Document mockDocument;

	@Test
	public void testGenerateOneArchiveMaticaCSV() {
		testProject = projectRepo.create("testProject");
		mockDocument = documentRepo.create(testProject, "testDocument", "txtUri", "pdfUri", "txtPath", "pdfPath", "Unassigned");
		
		ProjectLabelProfile profile = projectFieldProfileRepo.create(testProject, "Date Created", false, false, false, false, InputType.TEXT, null);
		MetadataFieldLabel dateCreatedLabel = metadataFieldLabelRepo.create("dc.date.created", profile);
		MetadataFieldGroup dateCreatedFields = metadataFieldGroupRepo.create(mockDocument, dateCreatedLabel);
		MetadataFieldValue dateCreatedValue = metadataFieldValueRepo.create("1962", dateCreatedFields);
		//TODO:  why is this necessary?
		dateCreatedFields.addValue(dateCreatedValue);
		//System.out.println("*** date created value: " + dateCreatedValue.getValue());
		mockDocument.addField(dateCreatedFields);
		mockDocument.setPublishedUriString("http://hdl.handle.net/1969.1/12345");
		
		CsvUtility.generateOneArchiveMaticaCSV(mockDocument, "nowhere", true);
		
		assertEquals("The parts column was incorrect.", CsvUtility.csvContents.get(1).get(0), "objects/testDocument");
		assertEquals("The dc.date.created was incorrect.", CsvUtility.csvContents.get(1).get(1), "1962");
		
		
//		for(ArrayList<String> strings : CsvUtility.csvContents)
//		{
//			for(String string : strings)
//			{
//				System.out.print(string + " | ");
//			}
//			System.out.println("");
//		}
	}

}
