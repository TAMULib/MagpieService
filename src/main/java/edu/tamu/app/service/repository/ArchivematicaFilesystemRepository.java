package edu.tamu.app.service.repository;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.utilities.CsvUtility;

public class ArchivematicaFilesystemRepository implements Preservation {

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectRepository projectRepository;

    private CsvUtility csvUtility;

    private static final Logger logger = Logger.getLogger(ArchivematicaFilesystemRepository.class);

    public ArchivematicaFilesystemRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        this.csvUtility = new CsvUtility(this.projectRepository);
    }

    @Override
    public Document push(Document document) throws IOException {

        String itemDirectoryPath = ASSETS_PATH + File.separator + document.getPath();

        File documentDirectory = new File(itemDirectoryPath);

        // make the top level container for the transfer package
        File archivematicaPackageDirectory = new File(getArchivematicaTopDirectory() + File.separator + document.getProject().getName() + "_" + document.getName());

        logger.info("Writing Archivematica Transfer Package for Document " + itemDirectoryPath + " to " + archivematicaPackageDirectory.getCanonicalPath());

        if (!archivematicaPackageDirectory.isDirectory())
            archivematicaPackageDirectory.mkdir();

        // make the logs, metadata, and objects subdirectories
        File metadataSubdirectory = new File(archivematicaPackageDirectory.getPath() + File.separator + "metadata");
        if (!metadataSubdirectory.isDirectory())
            metadataSubdirectory.mkdir();

        File logsSubdirectory = new File(archivematicaPackageDirectory.getPath() + File.separator + "logs");
        if (!logsSubdirectory.isDirectory())
            logsSubdirectory.mkdir();

        File objectsSubdirectory = new File(archivematicaPackageDirectory.getPath() + File.separator + "objects");
        if (!objectsSubdirectory.isDirectory())
            objectsSubdirectory.mkdir();

        // make the submissionDocumentation subdirectory in the metadata
        // subdirectory
        File submissionDocumentationSubdirectory = new File(metadataSubdirectory.getPath() + File.separator + "submissionDocumentation");
        if (!submissionDocumentationSubdirectory.isDirectory())
            submissionDocumentationSubdirectory.mkdir();

        // make the specific item subdirectory in the objects subdirectory
        File singleObjectSubdirectory = new File(objectsSubdirectory.getPath() + File.separator + document.getName());
        if (!singleObjectSubdirectory.isDirectory())
            singleObjectSubdirectory.mkdir();

        // assume for now that there are some number of tiffs in the document
        // directory and obtain the tiffs and md5 checksum from disk
        File[] tiffFiles = documentDirectory.listFiles(new OnlyTiff());

        File md5Listing = new File(metadataSubdirectory.getPath() + File.separator + "checksum.md5");

        for (File tiff : tiffFiles) {
            // make the md5hash listing from the tiffs
            addChecksumToListing(tiff, md5Listing);
        }

        // write the metadata csv in the metadata subdirectory
        // write the ArchiveMatica CSV for this document
        this.csvUtility.generateOneArchiveMaticaCSV(document, metadataSubdirectory.getPath());

        // copy the item directory and tiffs to the objects subdirectory
        for (File tiff : tiffFiles) {
            File destinationTiff = new File(singleObjectSubdirectory.getPath() + File.separator + tiff.getName());
            FileUtils.copyFile(tiff, destinationTiff);
        }

        Boolean startedTransfer = startArchivematicaTransfer(document, archivematicaPackageDirectory);

        if (startedTransfer) {
            document.setStatus("Published");
            documentRepo.update(document);
        } else {

        }

        return document;
    }

    private void addChecksumToListing(File tiffFile, File md5Listing) {

        String checksum = null;
        try {
            checksum = DigestUtils.md5Hex(new FileInputStream(tiffFile)) + " *" + tiffFile.getName();
            Path p = Paths.get(md5Listing.getPath());
            List<String> lines = md5Listing.exists() ? Files.readAllLines(p) : new ArrayList<String>();

            if (lines.isEmpty()) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy h:mm:ssa");
                LocalDateTime now = LocalDateTime.now();
                lines.add("# MD5 Generated by MAGPIE (https://github.com/TAMULib/MetadataAssignmentToolUI)");
                lines.add("# Generated " + dtf.format(now));
                lines.add("");
            }

            lines.add(checksum);
            Files.write(p, lines);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean startArchivematicaTransfer(Document document, File archivematicaPackageDirectory) throws IOException {
        // TODO: this shares code with the REST method in the DSpace repository
        // - consider pulling out into a utility method?
        // create the URL for the REST call
        URL restUrl;
        try {
            restUrl = new URL(getArchivematicaURL() + "/api/transfer/start_transfer/?username=" + getArchivematicaUsername() + "&api_key=" + getArchivematicaAPIKey());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            MalformedURLException murle = new MalformedURLException("Failed to initialize URL for Archivematica REST API call - the URL was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            throw murle;
        }

        // set up the connection for the REST call
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) restUrl.openConnection();
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to open connection to Archivematica REST API - URL was malformed. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            ProtocolException pe = new ProtocolException("Failed to set Archivematica request method to POST; that protocol for the request is invalid. {" + e.getMessage() + "}");
            pe.setStackTrace(e.getStackTrace());
            throw pe;
        }

        connection.setDoOutput(true);

        String itemDirectoryName = document.getProject().getName() + "_" + document.getName();
        String pathString = getArchivematicaTransferSourceLocationUUID() + ":" + getArchivematicaTransferLocationDirectoryName() + File.separator + itemDirectoryName;
        logger.info("Transfer package path string: " + pathString);
        String encodedPath = Base64Utils.encodeToString(pathString.getBytes());

        // Write post data by opening an output stream on the connection and
        // writing to it
        OutputStream os;
        try {
            logger.info("POSTing start transfer request to this URL: " + restUrl.toString());
            os = connection.getOutputStream();
        } catch (IOException e) {
            IOException ioe = new IOException("Could not open output stream to write the post data. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        String params = "";
        params += "name=" + document.getProject().getName() + "_" + document.getName() + "&type=standard" + "&paths[]=" + encodedPath;
        try {
            logger.info("POST request parameters being sent to archivematica: " + params);
            connection.getOutputStream().write(params.getBytes());

        } catch (IOException e) {
            IOException ioe = new IOException("Could not write data to the open output stream for the post. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        // Read response from item post
        StringBuilder response = new StringBuilder();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        } catch (IOException e) {
            IOException ioe = new IOException("Could not get input stream for a response from the connection of the POST request. Response message was \"" + connection.getResponseMessage() + "\" with this exception thrown: {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        // read the lines of the response into the... response :)
        String line;
        try {
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            IOException ioe = new IOException("Could not read a line from the response from the post. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        logger.info("POST request to archivematica resulted in response code of " + connection.getResponseCode() + " with response: " + response.toString());

        // Close streams
        try {
            br.close();
        } catch (IOException e) {
            IOException ioe = new IOException("Could not close the buffered reader from which we were getting the response from the post. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
        try {
            os.close();
        } catch (IOException e) {
            IOException ioe = new IOException("Could not close the output stream we were using to write to the post. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        // parse response to get a JSON node
        JsonNode responseNode = null;
        if (response.length() > 0) {
            try {
                responseNode = objectMapper.readTree(response.toString());
            } catch (IOException e) {
                IOException ioe = new IOException("Object mapper could not read the response from the post request into JSON. {" + e.getMessage() + "}");
                ioe.setStackTrace(e.getStackTrace());
                throw ioe;
            }
        }

        return (responseNode.get("message").asText().equals("Copy successful."));

    }

    public String getArchivematicaTopDirectory() throws IOException {
        String archivematicaDirectoryName = projectRepository.getSettingValues("archivematicaDirectoryName").get(0);
        String archivematicaTopDirectory;
        // if this is an absolute path (preceded with the file separator) treat
        // it as such
        if (archivematicaDirectoryName.startsWith(File.separator)) {
            archivematicaTopDirectory = archivematicaDirectoryName;
        } // otherwise, if this is not preceded with the file separator, then it
          // will be a relative path in the MAGPIE static directory.
        else {
            archivematicaTopDirectory = ASSETS_PATH + File.separator + archivematicaDirectoryName;
        }
        return archivematicaTopDirectory;
    }

    public String getArchivematicaURL() {
        return projectRepository.getSettingValues("archivematicaURL").get(0);
    }

    public String getArchivematicaUsername() {
        return projectRepository.getSettingValues("archivematicaUsername").get(0);
    }

    public String getArchivematicaAPIKey() {
        return projectRepository.getSettingValues("archivematicaAPIKey").get(0);
    }

    public String getArchivematicaTransferSourceLocationUUID() {
        return projectRepository.getSettingValues("archivematicaTransferSourceLocationUUID").get(0);
    }

    public String getArchivematicaTransferLocationDirectoryName() {
        return projectRepository.getSettingValues("archivematicaTransferLocationDirectoryName").get(0);
    }

    class OnlyTiff implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".tif") || name.toLowerCase().endsWith(".tiff");
        }
    }

    class OnlyMD5 implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".md5");
        }
    }

}
