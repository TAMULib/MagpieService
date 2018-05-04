package edu.tamu.app.utilities;

import java.io.BufferedWriter;
import java.io.File;
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

    // TODO: populate from configuration
    @SuppressWarnings("unused")
    private List<String> prioritizedLabels = null;

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

        String[] preferredElements = { "dc.date.created" };

        Map<String, List<String>> allMetadataKVPairsOnDocument = new HashMap<String, List<String>>();

        if (projectRepository.isPresent()) {
            Optional<String> publishedUrl = getPublishedUrl(document);
            if (publishedUrl.isPresent()) {
                List<String> identifierList = new ArrayList<String>();
                identifierList.add(publishedUrl.get());

                allMetadataKVPairsOnDocument.put("dc.identifier", identifierList);
            } else {
                logger.info("Document has not been published to its Project Repository.");
            }

        } else {
            logger.info("No Project Repository specified!");
        }

        List<MetadataFieldGroup> metadataFields = document.getFields();
        logger.info("Processing metadata fields from document " + document.getName());

        // First, compile the map of each key value pair. Identical keys are to
        // be repeated, except where there is a PRIORITIZED label that will take
        // precedence and overwrite all others
        List<String> fieldsNotToOverwrite = new ArrayList<String>();

        metadataFields.forEach(field -> {
            for (MetadataFieldValue metadataFieldValue : field.getValues()) {
                if (metadataFieldValue.getValue().trim().length() > 0) {

                    // if field.getLabel().getQualifiedName() is PRIORITIZED,
                    // then we will put it's value on top of what's there, overwriting.
                    // what's more, we'll remember, and we won't let anything
                    // else overwrite it later.
                    boolean fieldIsPreferred = false;
                    for (String elementName : preferredElements) {
                        if (field.getLabel().getName().equals(elementName)) {
                            fieldIsPreferred = true;
                            fieldsNotToOverwrite.add(field.getLabel().getUnqualifiedName());
                            break;
                        }
                    }

                    // put a new list with the single value under the key in case it's preferred or there's nothing there yet anyway
                    if (fieldIsPreferred || !allMetadataKVPairsOnDocument.containsKey(field.getLabel().getUnqualifiedName())) {
                        List<String> firstValue = new ArrayList<String>();
                        firstValue.add(metadataFieldValue.getValue());
                        allMetadataKVPairsOnDocument.put(field.getLabel().getUnqualifiedName(), firstValue);
                    }
                    // otherwise, append to the existing list, unless it is not to be overwritten as it contains a preferred field
                    else if (allMetadataKVPairsOnDocument.containsKey(field.getLabel().getUnqualifiedName()) && !fieldsNotToOverwrite.contains(field.getLabel().getUnqualifiedName())) {
                        allMetadataKVPairsOnDocument.get(field.getLabel().getUnqualifiedName()).add(metadataFieldValue.getValue());
                    }

                }
            }
        });

        // Second, add the header row. This has the "parts" field and all the metadata keys/labels
        // Have to turn our keySet of labels into a list, as we need order
        // guaranteed when making the first row (lables) and the second row (values)
        String labels[] = allMetadataKVPairsOnDocument.keySet().toArray(new String[0]);
        ArrayList<String> csvRow = new ArrayList<String>();
        csvRow.add("parts");
        for (int i = 0; i < elements.length; i++) {
            // writing the element
            for (String label : labels) {
                if (label.contains(elements[i])) {
                    // have to add a cell for every single occurrence of a value with the label
                    for (int k = 0; k < allMetadataKVPairsOnDocument.get(label).size(); k++)
                        csvRow.add(label);
                }
            }
        }

        csvContents.add(new ArrayList<String>(csvRow));
        csvRow.clear();
        csvRow.add("objects/" + document.getName());

        // Finally, write the data values we compiled the list of.
        for (int i = 0; i < elements.length; i++) {
            for (String label : labels) {
                if (label.contains(elements[i])) {
                    if (label.contains("parts")) {
                        csvRow.add("objects/" + document.getName());
                    } else {

                        for (int k = 0; k < allMetadataKVPairsOnDocument.get(label).size(); k++) {

                            csvRow.add(allMetadataKVPairsOnDocument.get(label).get(k));
                        }
                    }
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
