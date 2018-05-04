package edu.tamu.app.service.authority;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.utilities.FileSystemUtility;

public class CSVAuthority implements Authority {

    private static final Logger logger = Logger.getLogger(CSVAuthority.class);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    private static Map<String, String[]> headers;

    private static Map<String, Map<String, CSVRecord>> records;

    private ProjectAuthority projectAuthority;

    public CSVAuthority(ProjectAuthority projectAuthority) {
        this.projectAuthority = projectAuthority;
        headers = new HashMap<String, String[]>();
        records = new HashMap<String, Map<String, CSVRecord>>();
    }

    @Override
    public Document populate(Document document) {
        getPaths().forEach(path -> {
            if (CSVAuthority.records.get(path) == null) {
                cacheRecords(path);
            }
            String[] headers = CSVAuthority.headers.get(path);
            CSVRecord record = CSVAuthority.records.get(path).get(document.getName());
            if (record != null) {
                List<MetadataFieldGroup> mfgs = new ArrayList<MetadataFieldGroup>();
                for (String header : headers) {
                    if (header.equals(getIdentifier())) {
                        continue;
                    }
                    String cellValue = record.get(header);
                    if (cellValue != null) {
                        String[] values = cellValue.split(Pattern.quote(getDelimeter()));
                        if (values != null) {
                            MetadataFieldGroup mfg = document.getFieldByLabel(header);
                            if (mfg != null) {
                                for (String value : values) {
                                    if (!mfg.containsValue(value)) {
                                        Optional<String> defaultValue = Optional.of(mfg.getLabel().getProfile().getDefaultValue());
                                        if (value.length() == 0 && defaultValue.isPresent()) {
                                            value = defaultValue.get();
                                        }
                                        mfg.addValue(metadataFieldValueRepo.create(value, mfg));
                                    }
                                }
                                mfgs.add(mfg);
                            } else {
                                logger.debug("No MetadataFieldGroup with label: " + header);
                            }
                        }
                    } else {
                        logger.debug("No row with header: " + header);
                    }
                }
                document.setFields(mfgs);
            }
        });
        return document;
    }

    private void cacheRecords(String path) {
        logger.info("Caching " + path);
        try {
            CSVParser csvParser;
            if (CSVAuthority.headers.get(path) == null) {
                logger.info("Getting headers from " + path);
                csvParser = getParser(path);
                CSVAuthority.headers.put(path, getHeaders(csvParser.getRecords().get(0)));
                csvParser.close();
            }

            Map<String, CSVRecord> currentRecords = new HashMap<String, CSVRecord>();
            csvParser = getParser(path);
            logger.info("Preparing to process CSV records using identifier field " + getIdentifier());
            csvParser.getRecords().forEach(record -> {
                String filename = record.get(getIdentifier());
                logger.info("Processing record " + filename);
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
        String[] headers = CSVAuthority.headers.get(path);

        logger.info("CSV Parser for project authority " + projectAuthority.getName() + " reading spreadsheet at " + getCsvAbsolutePath(path));
        FileReader csvFileReader = new FileReader(getCsvAbsolutePath(path));

        if (headers == null) {
            csvParser = new CSVParser(csvFileReader, CSVFormat.RFC4180);
        } else {
            csvParser = new CSVParser(csvFileReader, CSVFormat.RFC4180.withHeader(headers));
        }
        return csvParser;
    }

    public List<String> getPaths() {
        return projectAuthority.getSettingValues("paths");
    }

    public String getIdentifier() {
        return projectAuthority.getSettingValues("identifier").get(0);
    }

    public String getDelimeter() {
        return projectAuthority.getSettingValues("delimeter").get(0);
    }

}
