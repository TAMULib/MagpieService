package edu.tamu.app.service.authority;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

    private ProjectAuthority projectAuthority;

    public CSVAuthority(ProjectAuthority projectAuthority) {
        this.projectAuthority = projectAuthority;
    }

    @Override
    public Document populate(Document document) {
        for (String path : getPaths()) {
            try {
                Instant start = Instant.now();
                String identifier = getIdentifier();
                String delimeter = getDelimeter();
                CSVParser csvParser = getParser(path);
                String[] headers = getHeaders(csvParser.getRecords().get(0));
                csvParser.close();
                csvParser = getParser(path, headers);
                for (CSVRecord record : csvParser.getRecords()) {
                    String filename = record.get(identifier);
                    logger.debug("Processing record " + filename);
                    if (filename != null) {
                        if (filename.equals(document.getName())) {
                            List<MetadataFieldGroup> mfgs = new ArrayList<MetadataFieldGroup>();
                            for (String header : headers) {
                                if (header.equals(identifier)) {
                                    continue;
                                }
                                String cellValue = record.get(header);
                                if (cellValue != null) {
                                    String[] values = cellValue.split(Pattern.quote(delimeter));
                                    if (values != null) {
                                        MetadataFieldGroup mfg = document.getFieldByLabel(header);
                                        if (mfg != null) {
                                            for (String value : values) {
                                                if (!mfg.containsValue(value)) {
                                                    Optional<String> defaultValue = Optional.of(mfg.getLabel().getProfile().getDefaultValue());
                                                    if (value.length() == 0 && defaultValue.isPresent()) {
                                                        value = defaultValue.get();
                                                    }
                                                    Instant innerStart = Instant.now();
                                                    mfg.addValue(metadataFieldValueRepo.create(value, mfg));
                                                    logger.info(Duration.between(innerStart, Instant.now()).toMillis() + " milliseconds to create metadata file value");
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
                            break;
                        }
                    } else {
                        logger.info("Record without filename found!");
                    }
                }
                csvParser.close();
                logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to find look for record in csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return document;
    }

    private String getCsvAbsolutePath(String path) throws IOException {
        return FileSystemUtility.getWindowsSafePathString(resourceLoader.getResource("classpath:" + path).getURL().getPath());
    }

    private CSVParser getParser(String path) throws FileNotFoundException, IOException {
        FileReader csvFileReader = new FileReader(getCsvAbsolutePath(path));
        return new CSVParser(csvFileReader, CSVFormat.RFC4180);
    }

    private CSVParser getParser(String path, String[] headers) throws FileNotFoundException, IOException {
        FileReader csvFileReader = new FileReader(getCsvAbsolutePath(path));
        return new CSVParser(csvFileReader, CSVFormat.RFC4180.withHeader(headers));
    }

    private String[] getHeaders(CSVRecord headersRecord) {
        String[] headers = new String[headersRecord.size()];
        for (int i = 0; i < headersRecord.size(); i++) {
            headers[i] = headersRecord.get(i);
        }
        return headers;
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
