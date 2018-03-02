package edu.tamu.app.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.PublishedLocation;

public class CsvUtility {

    private static final Logger logger = Logger.getLogger(CsvUtility.class);

    private Optional<ProjectRepository> projectRepository;

    public CsvUtility() {
        this.projectRepository = Optional.empty();
    }

    public CsvUtility(ProjectRepository projectRepository) {
        this();
        this.projectRepository = Optional.of(projectRepository);
    }

    public void generateCsvFile(List<List<String>> csvContents, String csvFileName) throws IOException {
        if(csvContents.size() > 0) {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvFileName));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
            
            for(List<String> row : csvContents) {
                csvPrinter.printRecord(row);
            }
            
            csvPrinter.flush();
            csvPrinter.close();
            if (logger.isDebugEnabled()) {
                logger.debug("Generated CSV file: " + csvFileName);
            }
        }
    }


    public List<List<String>> documentToList(Document document) throws IOException {

        List<List<String>> csvContents = new ArrayList<List<String>>();

        String[] elements = { "title", "creator", "subject", "description", "publisher", "contributor", "date", "type", "format", "identifier", "source", "language", "relation", "coverage", "rights" };

        Map<String, String> map = new HashMap<String, String>();
        if (projectRepository.isPresent()) {
            Optional<String> publishedUrl = getPublishedUrl(document);
            if (publishedUrl.isPresent()) {
                map.put("dc.identifier", publishedUrl.get());
            } else {
                logger.info("Unable to find Project Repositories published URL!");
            }

        } else {
            logger.info("No Project Repository specified!");
        }

        // map.put("dc.source","");
        // map.put("dc.relation","");
        // map.put("dc.coverage","");

        List<MetadataFieldGroup> metadataFields = document.getFields();

        metadataFields.forEach(field -> {
            String values = "";
            boolean firstPass = true;
            for (MetadataFieldValue medataFieldValue : field.getValues()) {
                if (medataFieldValue.getValue().trim().length() > 0) {
                    if (firstPass) {
                        values = medataFieldValue.getValue();
                        firstPass = false;
                    } else {
                        values += "||" + medataFieldValue.getValue();
                    }
                }
            }
            map.put(field.getLabel().getUnqualifiedName(), values);
        });

        // The first row is the "parts" field and all the metadata keys/labels
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

        generateCsvFile(csvContents, itemDirectory + File.separator + "metadata.csv");

        return csvContents;
    }

    private Optional<String> getPublishedUrl(Document document) {
        Optional<String> publishedUrl = Optional.empty();
        for (PublishedLocation publishedLocation : document.getPublishedLocations()) {
            if (publishedLocation.getRepository().equals(projectRepository.get())) {
                publishedUrl = Optional.of(publishedLocation.getUrl());
                break;
            }
        }
        return publishedUrl;
    }
}
