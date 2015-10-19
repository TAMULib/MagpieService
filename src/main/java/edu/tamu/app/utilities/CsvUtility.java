package edu.tamu.app.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

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
}
