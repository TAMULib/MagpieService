package edu.tamu.app.service.repository;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.io.BufferedReader;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.PublishedLocation;
import edu.tamu.app.model.PublishingType;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ResourceRepo;

public class DSpaceRepository extends PublishingRepository {

    private static final Logger logger = LoggerFactory.getLogger(DSpaceRepository.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    private ProjectRepository projectRepository;

    private Map<Long, Optional<Cookie>> authCookies;

    public DSpaceRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
        this.authCookies = new HashMap<Long, Optional<Cookie>>();
    }

    @Override
    public Document push(Document document) throws IOException {
        // login to get JSESSIONID
        login(document);

        // POST to create the item
        JsonNode createItemResponseNode = null;
        try {
            createItemResponseNode = createItem(document);
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            logout(document);

            RuntimeException serviceEx = new RuntimeException(e.getMessage());
            serviceEx.setStackTrace(e.getStackTrace());
            throw serviceEx;
        }

        String handleString = createItemResponseNode.get("handle").asText();
        String newItemIdString = createItemResponseNode.get("uuid").asText();

        broadcastDocument(document.getId(), PublishingType.ITEM, "Created Item using Internal ID " + newItemIdString + ".");

        // POST each of the bitstreams in this document to the newly created item
        addBitstreams(document, newItemIdString);

        // add new handle to document, change it's status to published, save it
        String publishedUrl;

        if (getRepoContextPath().length() > 0) {
            publishedUrl = getRepoUrl() + "/" + getRepoContextPath() + "/" + handleString;
        } else {
            publishedUrl = getRepoUrl() + "/" + handleString;
        }

        document.addPublishedLocation(new PublishedLocation(projectRepository, publishedUrl));
        broadcastDocument(document.getId(), PublishingType.MESSAGE, "Published at URL " + publishedUrl + ".");

        document.setStatus("Published");

        // logout to kill session
        logout(document);

        return documentRepo.update(document);
    }

    private void login(Document document) throws IOException {
        try {
            HttpClient httpClient = null;
            CookieStore httpCookieStore = new BasicCookieStore();
            HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCookieStore(httpCookieStore);
            httpClient = builder.build();

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("email", getEmail()));
            nameValuePairs.add(new BasicNameValuePair("password", getPassword()));

            HttpPost httpPost = new HttpPost(getRepoUrl() + "/rest/login");

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            httpClient.execute(httpPost);

            for (Cookie cookie : httpCookieStore.getCookies()) {
                if (cookie.getName().equals("JSESSIONID")) {
                  authCookies.put(document.getId(), Optional.of(cookie));
                }
            }

            if (!authCookies.containsKey(document.getId()) || !authCookies.get(document.getId()).isPresent()) {
                throw new RuntimeException("Unable to get cookie JSESSIONID from response!");
            }

            logger.info("Login successful. Authorization cookie: " + getCookieAsString(authCookies.get(document.getId()).get()));
            broadcastDocument(document.getId(), PublishingType.CONNECTION, "DSpace session established for repository " + projectRepository.getName() + ".");

        } catch (IOException e) {
            IOException ioe = new IOException("Failed to authenticate to DSpace. {" + e.getMessage() + "}");
            ioe.setStackTrace(e.getStackTrace());
            throw ioe;
        }
    }

    private void logout(Document document) throws IOException {
        doRESTRequest(document.getId(), new URL(getRepoUrl() + "/rest/logout"), "POST", "".getBytes(), "application/xml", "logout");
        authCookies.remove(document.getId());
        logger.info("Logout successful.");
        broadcastDocument(document.getId(), PublishingType.CONNECTION, "DSpace session closed for repository " + projectRepository.getName() + ".");
    }

    private JsonNode createItem(Document document) throws ParserConfigurationException, TransformerException, IOException {
        URL createItemUrl;
        try {
            createItemUrl = new URL(getRepoUrl() + "/rest/collections/" + getCollectionId() + "/items");
        } catch (MalformedURLException e) {
            logout(document);

            MalformedURLException murle = new MalformedURLException("Failed to create items; the REST URL to post the item was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            throw murle;
        }

        // produce the XML data from the document that we will post to the REST API
        String xmlDataToPost;
        try {
            xmlDataToPost = generateItemPostXMLFromDocument(document);
        } catch (ParserConfigurationException e) {
            logout(document);

            ParserConfigurationException pce = new ParserConfigurationException("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
            pce.setStackTrace(e.getStackTrace());
            throw pce;
        } catch (TransformerFactoryConfigurationError e) {
            logout(document);

            TransformerFactoryConfigurationError tfce = new TransformerFactoryConfigurationError("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
            tfce.setStackTrace(e.getStackTrace());
            throw tfce;
        } catch (TransformerException e) {
            logout(document);

            TransformerException te = new TransformerException("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
            te.setStackTrace(e.getStackTrace());
            throw te;
        }

        String taskDescription = "post item";

        return doRESTRequest(document.getId(), createItemUrl, "POST", xmlDataToPost.getBytes(), "application/xml", taskDescription);
    }

    private JsonNode doRESTRequest(Long documentId, URL restUrl, String method, byte[] postData, String contentTypeString, String taskDescription) throws IOException {
        logger.info("Making this REST request of DSpace: "+ taskDescription);
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

        connection.setRequestProperty("Cookie", getCookieAsString(authCookies.get(documentId).get()));

        logger.info("Attempting to connect to DSpace with Cookie = " + connection.getRequestProperty("Cookie"));

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

    private void addBitstreams(Document document, String itemId) throws IOException {
        addBitstreams(document, new Bitstreams(itemId, resourceRepo.findAllByDocumentProjectNameAndDocumentNameAndMimeType(document.getProject().getName(), document.getName(), "application/pdf")));
        addBitstreams(document, new Bitstreams(itemId, resourceRepo.findAllByDocumentProjectNameAndDocumentNameAndMimeType(document.getProject().getName(), document.getName(), "text/plain"), "TEXT"));
        addBitstreams(document, new Bitstreams(itemId, resourceRepo.findAllByDocumentProjectNameAndDocumentNameAndMimeType(document.getProject().getName(), document.getName(), "image/jpeg", "image/jpg", "image/jp2", "image/jpx", "image/bmp", "image/gif", "image/png", "image/svg", "image/tif", "image/tiff")));
    }

    private void addBitstreams(Document document, Bitstreams bitstreams) throws IOException {
        for (Resource resource : bitstreams.getResources()) {

            // *************************************
            // POST file
            // *************************************
            // add the bitstream
            URL addBitstreamUrl;
            try {
                addBitstreamUrl = new URL(getRepoUrl() + "/rest/items/" + bitstreams.getItemId() + "/bitstreams?name=" + URLEncoder.encode(resource.getName(), java.nio.charset.StandardCharsets.UTF_8.toString()));
            } catch (MalformedURLException e) {
                MalformedURLException murle = new MalformedURLException("Failed to add pdf bitstream; the REST URL to post the bitstreams was malformed. {" + e.getMessage() + "}");
                murle.setStackTrace(e.getStackTrace());
                cleanUpFailedPublish(document, bitstreams.getItemId());
                throw murle;
            }

            File file = new File(ASSETS_PATH + File.separator + resource.getPath());
            FileInputStream stream = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(stream);
            stream.close();

            ObjectNode bitstreamMetadataJson = null;
            try {
                bitstreamMetadataJson = (ObjectNode) doRESTRequest(document.getId(), addBitstreamUrl, "POST", bytes, resource.getMimeType(), "post bitstream");
            } catch (Exception e) {
                cleanUpFailedPublish(document, bitstreams.getItemId());
                throw e;
            }

            // *************************************
            // PUT bitstream metadata
            // *************************************
            // put a resource policy for member group access on the pdf bitstream
            // REST endpoint is PUT /bitstreams/{bitstream uuid} - Update metadata of
            // bitstream. You must put a Bitstream, does not alter the file/data
            // Fix up the PDF bitstream metadata to have new policy, etc.

            String uuid = bitstreamMetadataJson.get("uuid").asText();

            broadcastDocument(document.getId(), PublishingType.ATTACHMENT, "Associated item " + resource.getName() + " using Internal ID " + uuid + ".");

            ArrayNode policiesNode = bitstreamMetadataJson.putArray("policies");
            ObjectNode policyNode = objectMapper.createObjectNode();
            policyNode.put("action", "READ");
            policyNode.put("groupId", getGroupId());
            policyNode.put("rpType", "TYPE_CUSTOM");
            policiesNode.add(policyNode);

            if (bitstreams.getBundleName().isPresent()) {
                bitstreamMetadataJson.put("bundleName", bitstreams.getBundleName().get());
            }

            URL addPolicyUrl;
            try {
                addPolicyUrl = new URL(getRepoUrl() + "/rest/bitstreams/" + uuid);
            } catch (MalformedURLException e) {
                MalformedURLException murle = new MalformedURLException("Failed to update bitstream metadata; the REST URL to PUT the policy was malformed. {" + e.getMessage() + "}");
                murle.setStackTrace(e.getStackTrace());
                cleanUpFailedPublish(document, bitstreams.getItemId());
                throw murle;
            }

            try {
                doRESTRequest(document.getId(), addPolicyUrl, "PUT", bitstreamMetadataJson.toString().getBytes(), "application/json", "update bitstream metadata");
            } catch (Exception e) {
                cleanUpFailedPublish(document, bitstreams.getItemId());
                throw e;
            }

            broadcastDocument(document.getId(), PublishingType.MESSAGE, "Populated metadata for item " + resource.getName() + " using Internal ID " + uuid + ".");
        }
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

    private void cleanUpFailedPublish(Document document, String uuid) throws IOException {
        // delete the item in case there was an error along the way with all the requests.
        // REST endpoint is DELETE /items/{item uuid} - Delete item.

        logger.error("Error pushing to DSpace. Rolling back.");
        broadcastDocument(document.getId(), PublishingType.ALERT, "Error pushing item " + uuid + " to DSpace. Rolling back.");

        URL deleteItemUrl;
        try {
            deleteItemUrl = new URL(getRepoUrl() + "/rest/items/" + uuid);
        } catch (MalformedURLException e) {
            logout(document);

            MalformedURLException murle = new MalformedURLException("Failed to delete item " + uuid + "; the REST URL for the DELETE request was malformed. {" + e.getMessage() + "}");
            murle.setStackTrace(e.getStackTrace());
            throw murle;
        }

        try {
            doRESTRequest(document.getId(), deleteItemUrl, "DELETE", "".getBytes(), "application/json", "delete item");
        }
        catch (IOException e) {
            logout(document);
            throw e;
        }

        broadcastDocument(document.getId(), PublishingType.WARNING, "Cleaned up item " + uuid + ".");

        // logout to kill session
        logout(document);
    }

    class Bitstreams {

        private String itemId;

        private List<Resource> resources;

        private Optional<String> bundleName;

        public Bitstreams(String itemId, List<Resource> resources) {
            this.itemId = itemId;
            this.resources = resources;
            this.bundleName = Optional.empty();
        }

        public Bitstreams(String itemId, List<Resource> resources, String bundleName) {
            this(itemId, resources);
            this.bundleName = Optional.of(bundleName);
        }

        public String getItemId() {
            return itemId;
        }

        public List<Resource> getResources() {
            return resources;
        }

        public Optional<String> getBundleName() {
            return bundleName;
        }

    }

    public String getRepoUrl() {
        return getSettingValue("repoUrl");
    }

    public String getRepoContextPath() {
        return getSettingValue("repoContextPath");
    }

    public String getCollectionId() {
        return getSettingValue("collectionId");
    }

    public String getGroupId() {
        return getSettingValue("groupId");
    }

    public String getEmail() {
        return getSettingValue("email");
    }

    public String getPassword() {
        return getSettingValue("password");

    }

    private String getSettingValue(String key) {
        String value = projectRepository.getSettingValues(key).get(0);
        return (value != null) ? value:"";
    }

    private String getCookieAsString(Cookie cookie) {
        return String.join("=", cookie.getName(), cookie.getValue());
    }

}
