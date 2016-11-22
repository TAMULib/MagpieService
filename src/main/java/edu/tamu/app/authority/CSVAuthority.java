package edu.tamu.app.authority;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.utilities.FileSystemUtility;

@Service("NALTCSV")
public class CSVAuthority implements Authority {

    private static final Logger logger = Logger.getLogger(CSVAuthority.class);

    private static final String CELL_DELIMETER = "||";

    private static final String NALT_CSV_PATH = "config/csv/TAES-Misc-Publication-Collection-Metadata.csv";

    private static final String[] headers = new String[] { "filename", "dc.creator", "dc.title", "dc.relation.ispartof", "dc.publisher", "dc.date.issued", "dc.description.tableofcontents", "dc.subject", "subject.lcsh", "dc.subject.nalt", "dc.description", "dc.language", "dc.type", "dc.format", "dc.coverage.spatial", "dc.audience" };

    private Map<String, CSVRecord> records;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public Document populate(Document document) {
        if (records == null) {
            cacheRecords();
        }
        CSVRecord record = records.get(document.getName());
        if (record != null) {
            for (String header : headers) {
                if (header.equals("filename")) {
                    continue;
                }
                String cellValue = record.get(header);
                if (cellValue != null) {
                    String[] values = cellValue.split(Pattern.quote(CELL_DELIMETER));
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
        return document;
    }

    private void cacheRecords() {
        records = new HashMap<String, CSVRecord>();
        logger.info("Caching csv records for NALTCSV authority.");
        try {
            String csvPath = FileSystemUtility.getWindowsSafePathString(resourceLoader.getResource("classpath:" + NALT_CSV_PATH).getURL().getPath());
            CSVFormat.RFC4180.withHeader(headers).parse(new InputStreamReader(new FileInputStream(csvPath))).forEach(record -> {
                String filename = record.get("filename");
                if (filename != null) {
                    records.put(filename, record);
                } else {
                    logger.info("Record without filename found!");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
