package edu.tamu.app.service.authority;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.utilities.FileSystemUtility;

public class CSVAuthority implements Authority {

    private static final Logger logger = Logger.getLogger(CSVAuthority.class);

    private static final String[] headers = new String[] {
        "filename",
        "dc.creator",
        "dc.title",
        "dc.relation.ispartof",
        "dc.publisher",
        "dc.date.issued",
        "dc.description.tableofcontents",
        "dc.subject",
        "subject.lcsh",
        "dc.subject.nalt",
        "dc.description",
        "dc.language",
        "dc.type",
        "dc.format",
        "dc.coverage.spatial",
        "dc.audience"
    };

    private String delimeter;

    private List<String> paths;

    private Map<String, Map<String, CSVRecord>> records;

    @Autowired
    private ResourceLoader resourceLoader;

    public CSVAuthority(List<String> paths, String delimeter) {
        this.paths = paths;
        this.delimeter = delimeter;
        records = new HashMap<String, Map<String, CSVRecord>>();
    }

    @Override
    public Document populate(Document document) {
        paths.forEach(path -> {
            if (records.get(path) == null) {
                cacheRecords(path);
            }
            CSVRecord record = records.get(path).get(document.getName());
            if (record != null) {
                for (String header : headers) {
                    if (header.equals("filename")) {
                        continue;
                    }
                    String cellValue = record.get(header);
                    if (cellValue != null) {
                        String[] values = cellValue.split(Pattern.quote(delimeter));
                        if (values != null) {
                            MetadataFieldGroup fieldGroup = document.getFieldByLabel(header);
                            if (fieldGroup != null) {
                                for (String value : values) {
                                    if (!fieldGroup.containsValue(value)) {
                                        fieldGroup.addValue(new MetadataFieldValue(value, fieldGroup));
                                    }
                                }
                            } else {
                                logger.debug("No MetadataFieldGroup with label: " + header);
                            }
                        }
                    }
                }
            }
        });
        return document;
    }

    private void cacheRecords(String path) {
        logger.info("Caching " + path);
        try {
            Map<String, CSVRecord> currentRecords = new HashMap<String, CSVRecord>();
            String absoluteCsvPath = FileSystemUtility.getWindowsSafePathString(resourceLoader.getResource("classpath:" + path).getURL().getPath());
            CSVFormat.RFC4180.withHeader(headers).parse(new InputStreamReader(new FileInputStream(absoluteCsvPath))).forEach(record -> {
                String filename = record.get(headers[0]);
                if (filename != null) {
                    currentRecords.put(filename, record);
                } else {
                    logger.info("Record without filename found!");
                }
            });
            records.put(path, currentRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
