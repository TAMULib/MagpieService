package edu.tamu.app.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.utilities.CsvUtility;

@Service
public class DocumentPushService {
    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private CsvUtility csvUtility;

    @Value("${app.mount}")
    private String mount;

    @Value("${app.defaultCollectionId}")
    private String collectionId;

    @Value("${app.defaultGroupId}")
    private String groupId;

    @Value("${app.defaultRepoUrl}")
    private String repoUrl;

    @Value("${app.defaultRepoUIPath}")
    private String defaultRepoUIPath;

    @Value("${app.defaultDSpaceUsername}")
    private String username;

    @Value("${app.defaultDSpacePassword}")
    private String password;

    public Document push(Document document) throws Exception {
        // POST to create the item
        JsonNode createItemResponseNode = null;
        try {
            createItemResponseNode = createItem(document);
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            Exception serviceEx = new Exception(e.getMessage());
            serviceEx.setStackTrace(e.getStackTrace());
            throw serviceEx;
        }

        String handleString = createItemResponseNode.get("handle").asText();
        String newItemIdString = createItemResponseNode.get("id").asText();

        // POST each of the bitstreams in this document to the newly created
        // item
        addBitstreams(newItemIdString, document);

        // write the ArchiveMatica CSV for this document
        String directory = "";
        try {
            directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/archivematica/";
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to create items; Could not get Spring resource for the archivematica output directory on the mount. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            cleanUpFailedPublish(newItemIdString);
            throw ioe;
        }

        String archiveDirectoryName = directory + document.getProject().getName();
        File archiveDirectory = new File(archiveDirectoryName);
        if (archiveDirectory.isDirectory() == false) {
            archiveDirectory.mkdir();
        }
        String itemDirectoryName = archiveDirectoryName + "/" + document.getName();
        csvUtility.generateOneArchiveMaticaCSV(document, itemDirectoryName);

        // add new handle to document, change it's status to published, save it
        String publishedUriString = repoUrl + "/" + defaultRepoUIPath + "/" + handleString;
        document.setPublishedUriString(publishedUriString);

        document.setStatus("Published");

        document = documentRepo.save(document);

        return document;
    }

    private JsonNode createItem(Document document) throws ParserConfigurationException, TransformerException, IOException {
        URL createItemUrl;
        try {
            createItemUrl = new URL(repoUrl + "/rest/collections/" + collectionId + "/items");
        } catch (MalformedURLException e) {
            MalformedURLException murle = new MalformedURLException("Failed to create items; the REST URL to post the item was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            throw murle;
        }

        // produce the XML data from the document that we will post to the REST
        // API
        String xmlDataToPost;
        try {
            xmlDataToPost = generateItemPostXMLFromDocument(document);
        } catch (ParserConfigurationException e) {
            ParserConfigurationException pce = new ParserConfigurationException("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
            pce.setStackTrace(e.getStackTrace());
            throw pce;
        } catch (TransformerFactoryConfigurationError e) {
            TransformerFactoryConfigurationError tfce = new TransformerFactoryConfigurationError("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
            tfce.setStackTrace(e.getStackTrace());
            throw tfce;
        } catch (TransformerException e) {
            TransformerException te = new TransformerException("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
            te.setStackTrace(e.getStackTrace());
            throw te;
        }

        String taskDescription = "post item";

        return doRESTRequest(createItemUrl, "POST", xmlDataToPost.getBytes(), "application/xml", taskDescription);
    }

    private JsonNode doRESTRequest(URL restUrl, String method, byte[] postData, String contentTypeString, String taskDescription) throws IOException {
        // set up the connection for the REST call
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) restUrl.openConnection();
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to " + taskDescription + "; the REST URL to " + taskDescription + " was malformed. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        try {
            connection.setRequestMethod(method);
        } catch (ProtocolException e) {
            ProtocolException pe = new ProtocolException("Failed to " + taskDescription + "; the protocol for the request was invalid. {" + e.getMessage() + "}");
            pe.setStackTrace(e.getStackTrace());
            throw pe;
        }

        connection.setRequestProperty("Accept", "application/json");

        connection.setRequestProperty("Content-Type", contentTypeString);

        connection.setRequestProperty("Content-Length", String.valueOf(postData.length));

        String token = authenticateRest(username, password);
        connection.setRequestProperty("rest-dspace-token", token);

        connection.setDoOutput(true);

        // Write post data by opening an output stream on the connection and
        // writing to it
        OutputStream os;
        try {
            os = connection.getOutputStream();
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to " + taskDescription + "; Could not open output stream to write the post data. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        try {
            os.write(postData);
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to " + taskDescription + "; Could not write data to the open output stream for the post. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        // Read response from item post
        StringBuilder response = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to " + taskDescription + "; Could not get input stream for a response from the connection of the post request. {" + e.getMessage() + "}");
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
            IOException ioe = new IOException("Failed to " + taskDescription + "; Could not read a line from the response from the post. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        // Close streams
        try {
            br.close();
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to " + taskDescription + "; Could not close the buffered reader from which we were getting the response from the post. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
        try {
            os.close();
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to " + taskDescription + "; Could not close the output stream we were using to write to the post. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }

        // parse response to get a JSON node
        JsonNode responseNode = null;
        if (response.length() > 0) {
            try {
                responseNode = objectMapper.readTree(response.toString());
            } catch (IOException e) {
                IOException ioe = new IOException("Failed to " + taskDescription + "; Object mapper could not read the response from the post request into JSON. {" + e.getMessage() + "}");
                ioe.setStackTrace(e.getStackTrace());
                throw ioe;
            }
        }

        return responseNode;
    }

    private void addBitstreams(String itemId, Document document) throws IOException, ParserConfigurationException, TransformerException {

        // *************************************
        // POST PDF file
        // *************************************
        // add the bitstream for the primary pdf
        URL addBitstreamUrl;
        try {
            addBitstreamUrl = new URL(repoUrl + "/rest/items/" + itemId + "/bitstreams?name=" + document.getName() + ".pdf&description=primary_pdf");
        } catch (MalformedURLException e) {
            MalformedURLException murle = new MalformedURLException("Failed to add pdf bitstream; the REST URL to post the bitstreams was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            cleanUpFailedPublish(itemId);
            throw murle;
        }

        File pdfFile = appContext.getResource("classpath:static" + document.getPdfPath()).getFile();
        FileInputStream pdfFileStrm = new FileInputStream(pdfFile);
        byte[] pdfBytes = IOUtils.toByteArray(pdfFileStrm);

        ObjectNode pdfBitstreamJson = null;
        try {
            pdfBitstreamJson = (ObjectNode) doRESTRequest(addBitstreamUrl, "POST", pdfBytes, "application/pdf", "post bitstream");
        } catch (Exception e) {
            cleanUpFailedPublish(itemId);
            throw e;
        }

        String pdfBitstreamId = pdfBitstreamJson.get("id").asText();

        // *************************************
        // PUT PDF bitstream metadata
        // *************************************
        // put a resource policy for member group access on the pdf bitstream
        // REST endpoint is PUT /bitstreams/{bitstream id} - Update metadata of
        // bitstream. You must put a Bitstream, does not alter the file/data

        // Fix up the PDF bitstream metadata to have new policy, etc.
        ArrayNode policiesNode = pdfBitstreamJson.putArray("policies");
        ObjectNode policyNode = objectMapper.createObjectNode();
        policyNode.put("action", "READ");
        policyNode.put("groupId", groupId);
        policyNode.put("rpType", "TYPE_CUSTOM");
        policiesNode.add(policyNode);

        URL addPolicyUrl;
        try {
            addPolicyUrl = new URL(repoUrl + "/rest/bitstreams/" + pdfBitstreamId);
        } catch (MalformedURLException e) {
            MalformedURLException murle = new MalformedURLException("Failed to update pdf bitstream metadata; the REST URL to PUT the policy was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            cleanUpFailedPublish(itemId);
            throw murle;
        }

        try {
            doRESTRequest(addPolicyUrl, "PUT", pdfBitstreamJson.toString().getBytes(), "application/json", "update PDF bitstream metadata");
        } catch (Exception e) {
            cleanUpFailedPublish(itemId);
            throw e;
        }

        // *************************************
        // POST txt file
        // *************************************
        // add the bitstream for the extracted text
        try {
            addBitstreamUrl = new URL(repoUrl + "/rest/items/" + itemId + "/bitstreams?name=" + document.getName() + ".pdf.txt&description=ocr_text");
        } catch (MalformedURLException e) {
            MalformedURLException murle = new MalformedURLException("Failed to add bitstreams; the REST URL to post the bitstreams was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            cleanUpFailedPublish(itemId);
            throw murle;
        }

        File txtFile = appContext.getResource("classpath:static" + document.getTxtPath()).getFile();
        FileInputStream txtFileStrm = new FileInputStream(txtFile);
        byte[] txtBytes = IOUtils.toByteArray(txtFileStrm);

        ObjectNode txtBitstreamJson = null;
        try {
            txtBitstreamJson = (ObjectNode) doRESTRequest(addBitstreamUrl, "POST", txtBytes, "text/plain", "post bitstream");
        } catch (Exception e) {
            cleanUpFailedPublish(itemId);
            throw e;
        }

        // **************************************
        // PUT txt bitstream metadata
        // **************************************
        // put the txt bitstream into the TEXT bundle and set the READ policy to
        // the groupId.
        // REST endpoint is PUT /bitstreams/{bitstream id} - Update metadata of
        // bitstream. You must put a Bitstream, does not alter the file/data
        String txtBitstreamId = txtBitstreamJson.get("id").asText();
        policiesNode = txtBitstreamJson.putArray("policies");
        policyNode = objectMapper.createObjectNode();
        policyNode.put("action", "READ");
        policyNode.put("groupId", groupId);
        policyNode.put("rpType", "TYPE_CUSTOM");
        policiesNode.add(policyNode);
        txtBitstreamJson.put("bundleName", "TEXT");

        URL updateTXTBitstreamUrl;
        try {
            updateTXTBitstreamUrl = new URL(repoUrl + "/rest/bitstreams/" + txtBitstreamId);
        } catch (MalformedURLException e) {
            MalformedURLException murle = new MalformedURLException("Failed to modify txt bitstream; the REST URL to post the bitstream metadata was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            cleanUpFailedPublish(itemId);
            throw murle;
        }

        try {
            doRESTRequest(updateTXTBitstreamUrl, "PUT", txtBitstreamJson.toString().getBytes(), "application/json", "update TXT bitstream bundle");
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to update the text bitstream's bundle to TEXT. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            cleanUpFailedPublish(itemId);
            throw ioe;
        }

    }

    private String authenticateRest(String username, String password) throws IOException {

        HttpURLConnection con;
        String token = null;
        try {

            URL loginUrl = new URL(repoUrl + "/rest/login");

            con = (HttpURLConnection) loginUrl.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes("{\"email\": \"" + username + "\", \"password\": \"" + password + "\"}");
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;

            StringBuffer strBufRes = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                strBufRes.append(inputLine);
            }

            in.close();

            token = strBufRes.toString();
        } catch (IOException e) {
            IOException ioe = new IOException("Failed to authenticate to DSpace. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
        return token;
    }

    private String generateItemPostXMLFromDocument(Document document) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        org.w3c.dom.Document domDoc = docBuilder.newDocument();
        Element rootElement = domDoc.createElement("item");
        domDoc.appendChild(rootElement);

        for (MetadataFieldGroup fieldGroup : document.getFields()) {
            for (MetadataFieldValue value : fieldGroup.getValues()) {
                if (value.getValue() != null && !value.getValue().equals("")) {
                    Element metadata = domDoc.createElement("metadata");
                    rootElement.appendChild(metadata);

                    Element key = domDoc.createElement("key");
                    key.appendChild(domDoc.createTextNode(fieldGroup.getLabel().getName()));
                    metadata.appendChild(key);

                    Element valueElement = domDoc.createElement("value");
                    valueElement.appendChild(domDoc.createTextNode(value.getValue()));
                    metadata.appendChild(valueElement);

                    Element language = domDoc.createElement("language");
                    language.appendChild(domDoc.createTextNode("en_US"));
                    metadata.appendChild(language);
                }
            }
        }

        StringWriter stw = new StringWriter();
        Transformer serializer = TransformerFactory.newInstance().newTransformer();
        serializer.transform(new DOMSource(domDoc), new StreamResult(stw));

        return stw.toString();
    }

    private void cleanUpFailedPublish(String id) throws IOException {
        // delete the item in case there was an error along the way with all the
        // requests.
        // REST endpoint is DELETE /items/{item id} - Delete item.

        URL deleteItemUrl;
        try {
            deleteItemUrl = new URL(repoUrl + "/rest/items/" + id);
        } catch (MalformedURLException e) {
            MalformedURLException murle = new MalformedURLException("Failed to delete item " + id + "; the REST URL for the DELETE request was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            throw murle;
        }

        doRESTRequest(deleteItemUrl, "DELETE", "".getBytes(), "application/json", "delete item");
    }

}
