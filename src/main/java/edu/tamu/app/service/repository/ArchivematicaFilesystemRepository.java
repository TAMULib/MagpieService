package edu.tamu.app.service.repository;

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
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.tamu.app.model.Document;
import edu.tamu.app.utilities.CsvUtility;
import edu.tamu.app.utilities.FileSystemUtility;

public class ArchivematicaFilesystemRepository implements Repository {

    @Value("${app.mount}")
    private String mount;

    @Autowired
    private CsvUtility csvUtility;

    private String archivematicaTopDirectory;

    @Autowired
    private ResourceLoader resourceLoader;

    private String archivematicaURL;

    private String archivematicaUsername;

    private String archivematicaAPIKey;
    
    

    @Autowired
    private ObjectMapper objectMapper;

    public ArchivematicaFilesystemRepository(String archivematicaDirectoryName, String archivematicaURL, String archivematicaUsername, String archivematicaAPIKey) throws IOException {

        // if this is an absolute path (preceded with the file separator) treat
        // it as such
        if (archivematicaDirectoryName.startsWith(File.separator)) {
            archivematicaTopDirectory = archivematicaDirectoryName;
        } // otherwise, if this is not preceded with the file separator, then it
          // will be a relative path in the MAGPIE static directory. Perhaps it
          // could be symlinked to somewhere.
        else {
            archivematicaTopDirectory = resourceLoader.getResource("classpath:static").getURL().getPath()
                    + File.separator + mount + File.separator + archivematicaDirectoryName;
        }
        
        this.archivematicaURL = archivematicaURL;
        this.archivematicaUsername = archivematicaUsername;
        this.archivematicaAPIKey = archivematicaAPIKey;
        
        
    }

    @Override
    public Document push(Document document) throws IOException {

        Path itemDirectoryName = FileSystemUtility.getWindowsSafePath(
                resourceLoader.getResource("classpath:static").getURL().getPath() + document.getDocumentPath());

        File documentDirectory = itemDirectoryName.toFile();

        // make the top level container for the transfer package
        File archivematicaPackageDirectory = new File(archivematicaTopDirectory + File.separator
                + document.getProject().getName() + "_" + document.getName());

        System.out.println("Writing Archivematica Transfer Package for Document " + itemDirectoryName.toString()
                + " to " + archivematicaPackageDirectory.getCanonicalPath());

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
        File submissionDocumentationSubdirectory = new File(
                metadataSubdirectory.getPath() + File.separator + "submissionDocumentation");
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
            System.out.println("Found tiff " + tiff.getPath());
            // make the md5hash listing from the tiffs
            addChecksumToListing(tiff, md5Listing);
        }

        // write the metadata csv in the metadata subdirectory
        // write the ArchiveMatica CSV for this document
        csvUtility.generateOneArchiveMaticaCSV(document, metadataSubdirectory.getPath());

        // copy the item directory and tiffs to the objects subdirectory
        for (File tiff : tiffFiles) {
            File destinationTiff = new File(singleObjectSubdirectory.getPath() + File.separator + tiff.getName());
            FileUtils.copyFile(tiff, destinationTiff);
        }
        
        Boolean startedTransfer = startArchivematicaTransfer(document, archivematicaPackageDirectory);
        
        if(startedTransfer)
            document.setStatus("Published");
        else {
            
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
                lines.add("# MD5 Generated by MagPie (https://github.com/TAMULib/MetadataAssignmentToolUI)");
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
        
        //TODO:  this shares code with the REST method in the DSpace repository - consider pulling out into a utility method?
        //create the URL for the REST call
        URL restUrl;
        try {
            //restUrl = new URL("http://" + archivematicaURL + "/api/transfer/start_transfer/?username=" + archivematicaUsername + "&api_key=" + archivematicaAPIKey);
            restUrl = new URL("http://" + archivematicaURL + "/api/transfer/start_transfer/");
            
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
            ProtocolException pe = new ProtocolException("Failed to set Archivematica request method to GET; that protocol for the request is invalid. {" + e.getMessage() + "}");
            pe.setStackTrace(e.getStackTrace());
            throw pe;
        }
        
        connection.setDoOutput(true);
        
        connection.setRequestProperty("Accept", "application/json");
        //connection.setRequestProperty("Authorization", "Archivematica-API api_key=\"" + archivematicaAPIKey + "\", username=\"" + archivematicaUsername + "\"");
        
        
        ObjectNode on = objectMapper.createObjectNode();
        on.put("name", document.getName());
        on.put("type", "standard");
        //on.put("accession", "?");

        ArrayNode pathsNode = objectMapper.createArrayNode();
        
        UUID uuid = UUID.randomUUID();
        String pathString = uuid.toString() + ":" + archivematicaPackageDirectory.getPath();
        System.out.println("Transfer package path string: " + pathString);
        byte[] encodedBytes = Base64.getEncoder().encode(pathString.getBytes());
        pathsNode.add(encodedBytes);
        
        System.out.println("Transfer package encoded and then decoded: " + Base64.getDecoder().decode(encodedBytes) );
        
        on.set("paths", pathsNode);
        
        ArrayNode row_idsNode= objectMapper.createArrayNode();
        row_idsNode.add("");
        
        on.set("row_ids", row_idsNode);
        
        
        String json = objectMapper.writer().writeValueAsString(on);
        
        System.out.println("POST request to start Archivematia Transfer: " + json);
        

        // Write post data by opening an output stream on the connection and writing to it
        OutputStream os;
        try {
            os = connection.getOutputStream();
        } catch (IOException e) {
            IOException ioe = new IOException("Could not open output stream to write the post data. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        //TODO:  putting the transfer info in the params is one possibility for getting archivematica to start them
        String params = "";
        params += "username=" + archivematicaUsername
               +  "&api_key=" + archivematicaAPIKey
               +  "&name=" + document.getName()
               +  "&type=standard"
               +  "&paths=" + pathString  
               +  "&row_ids=[]\n"
               + json;
        try {
            os.write(params.getBytes());
            
            //os.write(json.getBytes());
            
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
            IOException ioe = new IOException("Could not get input stream for a response from the connection of the post request. Response message was \"" + connection.getResponseMessage() + "\" with this exception thrown: {" + e.getMessage() + "}");
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
        
        System.out.println("Archivematica transfer start message: " + responseNode.get("message") + "\nArchivematica transfer start response:" + connection.getResponseMessage());
        
        return (responseNode.get("message").equals("Copy successful."));
           
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
