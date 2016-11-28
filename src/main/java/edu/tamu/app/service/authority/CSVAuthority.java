package edu.tamu.app.service.authority;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
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

    private String identifier;

    private String delimeter;

    private List<String> paths;
    
    private Map<String, String[]> headers;

    private Map<String, Map<String, CSVRecord>> records;

    @Autowired
    private ResourceLoader resourceLoader;

    public CSVAuthority(List<String> paths, String identifier, String delimeter) {
        this.paths = paths;
        this.identifier = identifier;
        this.delimeter = delimeter;
        headers = new HashMap<String, String[]>();
        records = new HashMap<String, Map<String, CSVRecord>>();
    }

    @Override
    public Document populate(Document document) {
        paths.forEach(path -> {
            if (this.records.get(path) == null) {
                cacheRecords(path);
            }
            String[] headers = this.headers.get(path);
            CSVRecord record = this.records.get(path).get(document.getName());
            if (record != null) {
                for (String header : headers) {
                    if (header.equals(identifier)) {
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
                    else {
                        logger.debug("No row with header: " + header);
                    }
                }
            }
        });
        return document;
    }

    private void cacheRecords(String path) {
        logger.info("Caching " + path);
        try {            
            CSVParser csvParser;            
            if (this.headers.get(path) == null) {
                logger.info("Getting headers from " + path);
                csvParser = getParser(path);
                this.headers.put(path, getHeaders(csvParser.getRecords().get(0)));
                csvParser.close();
            }
            
            Map<String, CSVRecord> currentRecords = new HashMap<String, CSVRecord>();               
            csvParser = getParser(path);            
            csvParser.getRecords().forEach(record -> {
                String filename = record.get(identifier);
                if (filename != null) {
                    currentRecords.put(filename, record);
                } else {
                    logger.info("Record without filename found!");
                }
            });            
            csvParser.close();
            
            records.put(path, currentRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getHeaders(CSVRecord headersRecord) {
        String[] headers = new String[headersRecord.size()];
        for (int i = 0; i < headersRecord.size(); i++) {
            headers[i] = headersRecord.get(i);
        }
        return headers;
    }

    private String getCsvAbsolutePath(String path) throws IOException {
        return FileSystemUtility.getWindowsSafePathString(resourceLoader.getResource("classpath:" + path).getURL().getPath());
    }

    private CSVParser getParser(String path) throws FileNotFoundException, IOException {
        CSVParser csvParser;
        String[] headers = this.headers.get(path);
        if(headers == null) {
            csvParser = new CSVParser(new FileReader(getCsvAbsolutePath(path)), CSVFormat.RFC4180);
        }
        else {
            csvParser = new CSVParser(new FileReader(getCsvAbsolutePath(path)), CSVFormat.RFC4180.withHeader(headers));
        }
        return csvParser;
    }

}
