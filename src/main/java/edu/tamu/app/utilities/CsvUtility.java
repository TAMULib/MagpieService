package edu.tamu.app.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        if (csvContents.size() > 0) {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(csvFileName));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

            for (List<String> row : csvContents) {
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

        List<Metadatum> allMetadataKVPairsOnDocument = new ArrayList<Metadatum>();

        if (projectRepository.isPresent()) {
            Optional<String> publishedUrl = getPublishedUrl(document);
            if (publishedUrl.isPresent()) {
                allMetadataKVPairsOnDocument.add(new Metadatum("dc.identifier", publishedUrl.get()));
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

        // First, compile the list of each key value pair. Identical keys are to
        // be repeated.
        metadataFields.forEach(field -> {
            for (MetadataFieldValue medataFieldValue : field.getValues()) {
                if (medataFieldValue.getValue().trim().length() > 0) {
                    allMetadataKVPairsOnDocument.add(new Metadatum(field.getLabel().getUnqualifiedName(), medataFieldValue.getValue()));
                }
            }

        });

        // Second, add the header row. This has the "parts" field and all the
        // metadata keys/labels
        ArrayList<String> csvRow = new ArrayList<String>();
        csvRow.add("parts");
        for (int i = 0; i < elements.length; i++) {
            // writing the element
            for (Metadatum metadatum : allMetadataKVPairsOnDocument) {
                if (metadatum.getLabel().contains(elements[i])) {
                    csvRow.add(metadatum.getLabel());
                }
            }
        }

        csvContents.add(new ArrayList<String>(csvRow));
        csvRow.clear();
        csvRow.add("objects/" + document.getName());

        // Finally, write the data values we compiled the list of.
        for (int i = 0; i < elements.length; i++) {
            for (Metadatum metadatum : allMetadataKVPairsOnDocument) {
                if (metadatum.getLabel().contains(elements[i])) {

                    if (metadatum.getLabel().contains("parts")) {
                        metadatum.setValue("objects/" + document.getName());
                    }
                    csvRow.add(metadatum.getValue());
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

    private class Metadatum {
        private final String label;
        private String value;

        public Metadatum(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
