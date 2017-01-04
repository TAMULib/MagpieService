package edu.tamu.app.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;

@Service
public class CsvUtility {

    private static final Logger logger = Logger.getLogger(CsvUtility.class);

    public void generateCsvFile(List<List<String>> csvContents, String csvFileName) throws IOException {
        if (csvContents.size() > 0) {
            FileWriter fileWriter = new FileWriter(csvFileName);
            fileWriter.append(generateCsv(csvContents));
            fileWriter.flush();
            fileWriter.close();
            if (logger.isDebugEnabled()) {
                logger.debug("Generated CSV file: " + csvFileName);
            }
        }
    }

    private String generateCsv(List<List<String>> csvContents) {
        String csv = "";
        for (List<String> row : csvContents) {
            for (String value : row) {
                logger.info(value);
                csv += "\"" + value + "\",";
            }
            csv = csv.substring(0, csv.length() - 1);
            csv += "\n";
        }
        return csv;
    }

    public List<List<String>> documentToList(Document document) throws IOException {

        List<List<String>> csvContents = new ArrayList<List<String>>();

        String[] elements = { "title", "creator", "subject", "description", "publisher", "contributor", "date", "type", "format", "identifier", "source", "language", "relation", "coverage", "rights" };

        Map<String, String> map = new HashMap<String, String>();
        map.put("dc.identifier", document.getPublishedUriString());
        // map.put("dc.source","");
        // map.put("dc.relation","");
        // map.put("dc.coverage","");

        List<MetadataFieldGroup> metadataFields = document.getFields();

        metadataFields.forEach(field -> {
            String values = "";
            boolean firstPass = true;
            for (MetadataFieldValue medataFieldValue : field.getValues()) {
                if (firstPass) {
                    values = medataFieldValue.getValue();
                    firstPass = false;
                } else {
                    values += "||" + medataFieldValue.getValue();
                }
            }
            map.put(field.getLabel().getName(), values);
        });

        ArrayList<String> csvRow = new ArrayList<String>();
        csvRow.add("parts");
        for (int i = 0; i < elements.length; i++) {
            // writing the element
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().contains(elements[i])) {
                    csvRow.add(entry.getKey());
                }
            }
        }

        csvContents.add(new ArrayList<String>(csvRow));
        csvRow.clear();
        csvRow.add("objects/" + document.getName());

        // writing the data values
        for (int i = 0; i < elements.length; i++) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().contains(elements[i])) {

                    if (entry.getKey().contains("parts")) {
                        map.put(entry.getKey(), "objects/" + document.getName());
                    }
                    csvRow.add(entry.getValue());
                }
            }
        }
        csvContents.add(new ArrayList<String>(csvRow));
        csvRow.clear();

        return csvContents;
    }

    public List<List<String>> generateOneArchiveMaticaCSV(Document document, String itemDirectoryName) throws IOException {

        List<List<String>> csvContents = documentToList(document);

        File itemDirectory = new File(itemDirectoryName);
        if (itemDirectory.isDirectory() == false) {
            itemDirectory.mkdir();
        }

        generateCsvFile(csvContents, itemDirectory + "/metadata_" + System.currentTimeMillis() + ".csv");

        return csvContents;
    }

}
