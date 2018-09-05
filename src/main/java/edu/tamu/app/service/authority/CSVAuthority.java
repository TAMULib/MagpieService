package edu.tamu.app.service.authority;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                boolean found = false;
                String identifier = getIdentifier();
                String delimeter = Pattern.quote(getDelimeter());
                String documentName = document.getName();
                CSVParser csvParser = getParser(path);
                List<String> headers = csvParser.getHeaderMap().keySet().stream().filter(h -> !h.equals(identifier)).collect(Collectors.toList());
                for (CSVRecord record : csvParser.getRecords()) {
                    String filename = record.get(identifier);
                    if (filename != null) {
                        if (filename.equals(documentName)) {
                            found = true;
                            List<MetadataFieldGroup> mfgs = new ArrayList<MetadataFieldGroup>();
                            for (String header : headers) {
                                String cellValue = record.get(header);
                                if (cellValue != null) {
                                    String[] values = cellValue.split(delimeter);
                                    MetadataFieldGroup mfg = document.getFieldByLabel(header);
                                    if (mfg != null) {
                                        String defaultValue = mfg.getLabel().getProfile().getDefaultValue();
                                        for (String value : values) {
                                            if (!mfg.containsValue(value)) {
                                                if (value.length() == 0 && defaultValue != null) {
                                                    value = defaultValue;
                                                }
                                                Instant innerStart = Instant.now();
                                                mfg.addValue(metadataFieldValueRepo.create(value, mfg));
                                                logger.info(Duration.between(innerStart, Instant.now()).toMillis() + " milliseconds to create metadata file value");
                                            }
                                        }
                                        mfgs.add(mfg);
                                    } else {
                                        logger.debug("Could not find metadata field group with label: " + header);
                                    }
                                } else {
                                    logger.debug("Record does not a value for column " + header + "!");
                                }
                            }
                            document.setFields(mfgs);
                            break;
                        }
                    } else {
                        logger.debug("Record does not have the expected identifier!");
                    }
                }
                csvParser.close();
                if (found) {
                    logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to find for record in csv");
                } else {
                    logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to look for record in csv");
                    logger.debug("No record with identifier " + documentName + " found!");
                }

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
        return new CSVParser(csvFileReader, CSVFormat.RFC4180.withFirstRecordAsHeader());
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
