package edu.tamu.app.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;

public class CsvUtility {
	
	public ArrayList<ArrayList<String>> csvContents;
	
	private static final Logger logger = Logger.getLogger(CsvUtility.class);
	
	public CsvUtility() {
		this.csvContents = new ArrayList<ArrayList<String>>();
	}
	
	public void appendRow(ArrayList<String> row) {
		this.csvContents.add(new ArrayList<String>(row));
	}
	
	public void generateCsvFile(String csvFileName) throws IOException {
		if (this.csvContents.size() > 0) {
			FileWriter fileWriter = new FileWriter(csvFileName);
			fileWriter.append(this.generateCsv());
			fileWriter.flush();
			fileWriter.close();
			if (logger.isDebugEnabled()) {
				logger.debug("Generated CSV file: "+csvFileName);
			}
		}
	}
	
	public String getCsvString() {
		return this.generateCsv();
	}
	
	private String generateCsv() {
		String csv = "";
		for (ArrayList<String> row : this.csvContents) {
			for (String value : row) {
				logger.info(value);
				csv += "\""+value+"\",";
			}
			csv = csv.substring(0,csv.length()-1);
			csv += "\n";
		}
		return csv;
	}
	
	
	
	public static void generateOneArchiveMaticaCSV(Document document, String itemDirectoryName)
	{
		String [] elements = {"title","creator", "subject","description", "publisher","contributor", "date","type", "format","identifier", "source", "language", "relation","coverage", "rights"};		
		
		
	
		Date date  = new Date();
		String formatDate = new SimpleDateFormat("YYYY/mm/dd").format(date);
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("dc.identifier","");
		map.put("dc.source","");
		map.put("dc.relation","");
		map.put("dc.coverage","");

		
		File itemDirectory = new File(itemDirectoryName);
		if (itemDirectory.isDirectory() == false) {
			itemDirectory.mkdir();
		}
		Set<MetadataFieldGroup> metadataFields = document.getFields(); 			
		
		metadataFields.forEach(field -> {
				String values ="";
				boolean firstPass = true;
				for(MetadataFieldValue medataFieldValue : field.getValues()) {					
					if(firstPass) {
						values = medataFieldValue.getValue();
						firstPass = false;
					}
					else {
						values += "||" +  medataFieldValue.getValue();
					}					
				}
				map.put(field.getLabel().getName(),values);
			});

		// writing to the ArchiveMatica format metadata csv file
		try{
			CsvUtility csvUtil = new CsvUtility();
			ArrayList<String> csvRow = new ArrayList<String>();
			csvRow.add("parts");
			for(int i=0;i<elements.length;i++) {
				//writing the element 
				for(Map.Entry<String, String> entry : map.entrySet()) {
					if(entry.getKey().contains(elements[i])) {						
						csvRow.add(entry.getKey());
					}
				}
			}
			csvUtil.appendRow(csvRow);
			csvRow.clear();
			csvRow.add("objects/"+document.getName());
			//writing the data values
			for(int i=0;i<elements.length;i++) {
				for(Map.Entry<String,String> entry : map.entrySet()) {
					if(entry.getKey().contains(elements[i])) {
						
						if(entry.getKey().contains("parts")) {
							map.put(entry.getKey(), "objects/"+document.getName());
						}
						if(entry.getKey().contains("date")) {
							map.put(entry.getKey(), formatDate);
						}
						if(entry.getKey().contains("type")) {
							map.put(entry.getKey(), "Archival Information Package");
						}
						if(entry.getKey().contains("format")) {
							map.put(entry.getKey(), "Image/tiff");
						}
						if(entry.getKey().contains("language")) {
							map.put(entry.getKey(), "English");
						}
						csvRow.add(entry.getValue());
					}
				}
			}
			csvUtil.appendRow(csvRow);
			csvRow.clear();
			csvUtil.generateCsvFile(itemDirectory+"/metadata_"+System.currentTimeMillis()+".csv");
		} catch(Exception ioe) {
			logger.error(ioe);
		}
	}
	
	
	
}
