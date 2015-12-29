package edu.tamu.app.service;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

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
import javax.xml.transform.TransformerConfigurationException;
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

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.utilities.CsvUtility;
import edu.tamu.framework.model.ApiResponse;

@Service
public class DocumentPushService 
{
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Value("${app.mount}") 
   	private String mount;
	
	@Value("${app.defaultCollectionId}")
	private String collectionId;
	
	@Value("${app.defaultRepoUrl}")
	private String repoUrl;
	
	@Value("${app.defaultRepoUIPath}")
	private String defaultRepoUIPath;
	
	@Value("${app.defaultDSpaceUsername}")
	private String username;
	
	@Value("${app.defaultDSpacePassword}")
	private String password;
	
	
	
	
	
	public Document push(Document document) throws Exception
	{
		JsonNode createItemResponseNode = null;
		try {
			createItemResponseNode = createItem(document);
		} catch (ParserConfigurationException | TransformerException | IOException e) {
			Exception serviceEx = new Exception(e.getMessage());
			serviceEx.setStackTrace(e.getStackTrace());
			throw serviceEx;
		}
        
        System.out.println("And here is your response:\n\n" + createItemResponseNode.toString() +"\n");
        
        String handleString = createItemResponseNode.get("handle").asText();
        String newItemIdString = createItemResponseNode.get("id").asText();
        
        //POST each of the bitstreams in this document to the newly created item
        addBitstreams(newItemIdString, document);
        
        //write the ArchiveMatica CSV for this document
        String directory = "";
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/archivematica/";
		} catch (IOException e) {
			IOException ioe = new IOException("Failed to create items; Could not get Spring resource for the archivematica output directory on the mount. {" + e.getMessage() + "}");
			ioe.setStackTrace(e.getStackTrace());
			throw ioe;
		}
		
		String archiveDirectoryName = directory+document.getProject().getName();
        File archiveDirectory = new File(archiveDirectoryName);
		if (archiveDirectory.isDirectory() == false) {
			archiveDirectory.mkdir();
		}
        String itemDirectoryName = archiveDirectoryName + "/" + document.getName();
        CsvUtility.generateOneArchiveMaticaCSV(document, itemDirectoryName);
        
        //add new handle to document, change it's status to published, save it
        String publishedUriString = repoUrl + "/" + defaultRepoUIPath + "/" + handleString;
        document.setPublishedUriString(publishedUriString);
        
        document.setStatus("Published");
        
        document = documentRepo.save(document);
        
        //return the document in an ApiResponse
        return document;

	}
	
	private JsonNode createItem(Document document) throws ParserConfigurationException, TransformerException, IOException
	{
		URL createItemUrl;
		try {
			createItemUrl = new URL(repoUrl+"/rest/collections/"+collectionId+"/items");
		} catch (MalformedURLException e) {
			MalformedURLException murle = new MalformedURLException("Failed to create items; the REST URL to post the item was malformed. {" + e.getMessage() + "}" );
			murle.setStackTrace(e.getStackTrace());
			throw murle;
		}
		
		
		//produce the XML data from the document that we will post to the REST API
		String xmlDataToPost;
		try {
			xmlDataToPost = generateItemPostXMLFromDocument(document);
		} catch (ParserConfigurationException e)
		{
			ParserConfigurationException pce = new ParserConfigurationException("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
			pce.setStackTrace(e.getStackTrace());
			throw pce;
		}
	    catch ( TransformerFactoryConfigurationError e )
	    {
	    		TransformerFactoryConfigurationError tfce = new TransformerFactoryConfigurationError("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
	    		tfce.setStackTrace(e.getStackTrace());
	    		throw tfce;
	    }
	    catch( TransformerException e) 
	    {
	    		TransformerException te = new TransformerException("Failed to create items; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
	    		te.setStackTrace(e.getStackTrace());
	    		throw te;	    		
	    }
		
		String taskDescription = "post item";
		
		JsonNode responseNode = doPost(createItemUrl, xmlDataToPost.getBytes(), "application/xml", taskDescription);
		
		return responseNode;
	}
	
	
	private JsonNode doPost(URL restUrl, byte[] postData, String contentTypeString, String taskDescription) throws IOException
	{
		//set up the connection for the REST call
	    HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) restUrl.openConnection();
		} catch (IOException e) {
			IOException ioe = new IOException("Failed to " + taskDescription + "; the REST URL to " + taskDescription + " was malformed. {" + e.getMessage() + "}");
			ioe.setStackTrace(e.getStackTrace());
			throw ioe;
		}
		
		try {
			connection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			ProtocolException pe = new ProtocolException("Failed to " + taskDescription + "; the protocol for the request was invalid. {" + e.getMessage() + "}");
			pe.setStackTrace(e.getStackTrace());
			throw pe;
		}
		
		connection.setRequestProperty("Accept", "application/json");
		
		connection.setRequestProperty("Content-Type", contentTypeString);
		
		connection.setRequestProperty("Content-Length",  String.valueOf(postData.length));
		
		String token = authenticateRest(username, password);
		connection.setRequestProperty("rest-dspace-token", token);
		
		connection.setDoOutput(true);
		
		// Write post data by opening an output stream on the connection and writing to it
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
				
	    
	    System.out.println("Got response code " + connection.getResponseCode());
	    //System.out.println("Got error stream " + connection.getErrorStream())
	    
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
	      
		//read the lines of the response into the... response :)
	    String line;
	    try {
			while ( (line = br.readLine()) != null) {
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
	    
	    //parse response to get a JSON node
	    JsonNode responseNode = null;
	    if (response.length() > 0)
	    {
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
		
		//add the bitstream for the primary pdf
		URL addBitstreamUrl;
		try {
			addBitstreamUrl = new URL(repoUrl+"/rest/items/"+itemId+"/bitstreams?name="+document.getName()+".pdf&description=primary_pdf");
		} catch (MalformedURLException e) {
			MalformedURLException murle = new MalformedURLException("Failed to add pdf bitstream; the REST URL to post the bitstreams was malformed. {" + e.getMessage() + "}" );
			murle.setStackTrace(e.getStackTrace());
			throw murle;
		}
		
		File pdfFile = appContext.getResource("classpath:static" + document.getPdfPath()).getFile();
		FileInputStream pdfFileStrm = new FileInputStream(pdfFile);
		byte[] pdfBytes = IOUtils.toByteArray(pdfFileStrm);
		
		JsonNode pdfBitstreamJson = doPost(addBitstreamUrl, pdfBytes, "application/pdf", "post bitstream");
		
		System.out.println(pdfBitstreamJson.toString());
		
		String pdfBitstreamId = pdfBitstreamJson.get("id").asText();
		
		System.out.println("Time to add a policy to the new pdf bitstream of id: " + pdfBitstreamId);
		
//		//put the pdf bitstream into the ORIGINAL bundle and give it primary status
//		// endpoint is /bitstreams/{bitstream id}
//		URL modifyBitstreamUrl;
//		try {
//			modifyBitstreamUrl = new URL(repoUrl+"/bitstreams/" + pdfBitstreamId);
//		}catch (MalformedURLException e) {
//			MalformedURLException murle = new MalformedURLException("Failed to modify pdf bitstream; the REST URL to post the bitstream metadata was malformed. {" + e.getMessage() + "}" );
//			murle.setStackTrace(e.getStackTrace());
//			throw murle;
//		}
//		
//		//produce the XML data from the document that we will post to the REST API
//		String bitstreamXMLToPost;
//		try {
//			bitstreamXMLToPost = generateBitstreamPostXML("ORIGINAL", true);
//		} catch (ParserConfigurationException e)
//		{
//			ParserConfigurationException pce = new ParserConfigurationException("Failed to update PDF bitstream; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
//			pce.setStackTrace(e.getStackTrace());
//			throw pce;
//		}
//	    catch ( TransformerFactoryConfigurationError e )
//	    {
//	    		TransformerFactoryConfigurationError tfce = new TransformerFactoryConfigurationError("Failed to update PDF bitstream; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
//	    		tfce.setStackTrace(e.getStackTrace());
//	    		throw tfce;
//	    }
//	    catch( TransformerException e) 
//	    {
//	    		TransformerException te = new TransformerException("Failed to update PDF bitstream; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
//	    		te.setStackTrace(e.getStackTrace());
//	    		throw te;	    		
//	    }
//		
//		String taskDescription = "update PDF bitstream";
//		
//		JsonNode responseNode = doPost(modifyBitstreamUrl, bitstreamXMLToPost.getBytes(), "application/xml", taskDescription);

		
		
		
		
		
		//put a resource policy for member group access on the pdf bitstream
		// endpoint is /bitstreams/{bitstream id}/policy
		//produce the XML data from the document that we will post to the REST API
		String policyXMLToPost;
		try {
			policyXMLToPost = generatePolicyPostXML(pdfBitstreamId, "5");
		} catch (ParserConfigurationException e)
		{
			ParserConfigurationException pce = new ParserConfigurationException("Failed to update PDF bitstream; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
			pce.setStackTrace(e.getStackTrace());
			throw pce;
		}
	    catch ( TransformerFactoryConfigurationError e )
	    {
	    		TransformerFactoryConfigurationError tfce = new TransformerFactoryConfigurationError("Failed to update PDF bitstream; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
	    		tfce.setStackTrace(e.getStackTrace());
	    		throw tfce;
	    }
	    catch( TransformerException e) 
	    {
	    		TransformerException te = new TransformerException("Failed to update PDF bitstream; Could not transform document metadata into XML for the post. {" + e.getMessage() + "}");
	    		te.setStackTrace(e.getStackTrace());
	    		throw te;	    		
	    }
		URL addPolicyUrl;
		try {
			addPolicyUrl = new URL(repoUrl+"/rest/bitstreams/" + pdfBitstreamId + "/policy");
		}catch (MalformedURLException e) {
			MalformedURLException murle = new MalformedURLException("Failed to modify pdf bitstream; the REST URL to post the bitstream metadata was malformed. {" + e.getMessage() + "}" );
			murle.setStackTrace(e.getStackTrace());
			throw murle;
		}
		
		//System.out.println("Attempt to post: " + policyXMLToPost);
		
		String policyJSONToPost = "{\"action\":\"READ\",\"epersonId\":-1,\"groupId\":5,\"resourceId\":" + pdfBitstreamId + ",\"resourceType\":\"bitstream\",\"rpDescription\":null,\"rpName\":null,\"rpType\":\"TYPE_CUSTOM\",\"startDate\":null,\"endDate\":null}";
		System.out.println("Attempt to post: " + policyJSONToPost);
		JsonNode policyResponseNode = doPost(addPolicyUrl, policyJSONToPost.getBytes(), "application/json", "add policy");
		
		
		
		//add the bitstream for the extracted text
		try {
			addBitstreamUrl = new URL(repoUrl+"/rest/items/"+itemId+"/bitstreams?name="+document.getName()+".pdf.txt&description=ocr_text");
		} catch (MalformedURLException e) {
			MalformedURLException murle = new MalformedURLException("Failed to add bitstreams; the REST URL to post the bitstreams was malformed. {" + e.getMessage() + "}" );
			murle.setStackTrace(e.getStackTrace());
			throw murle;
		}
		
		File txtFile = appContext.getResource("classpath:static" + document.getTxtPath()).getFile();
		FileInputStream txtFileStrm = new FileInputStream(txtFile);
		byte[] txtBytes = IOUtils.toByteArray(txtFileStrm);
		
		doPost(addBitstreamUrl, txtBytes, "text/plain", "post bitstream");
		
		//put the txt bitstream into the TEXT bundle
		
		//put a resource policy for member group access on the txt bitstream
	}
	
	
	private String authenticateRest(String username, String password) {
		
		HttpURLConnection con;
		String token = null;
		try {
			
			URL loginUrl = new URL(repoUrl+"/rest/login");
			
			con = (HttpURLConnection) loginUrl.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			
			//Send request
		    DataOutputStream wr = new DataOutputStream (con.getOutputStream ());
		    wr.writeBytes ("{\"email\": \"" + username + "\", \"password\": \"" + password +"\"}");
		    wr.flush ();
		    wr.close ();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String inputLine;
			
			StringBuffer strBufRes = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				strBufRes.append(inputLine);
			}
			
			in.close();
			
			token = strBufRes.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}
	
	
	private String generateItemPostXMLFromDocument(Document document) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        org.w3c.dom.Document domDoc = docBuilder.newDocument();
        Element rootElement = domDoc.createElement("item");
        domDoc.appendChild(rootElement);

        for(MetadataFieldGroup fieldGroup : document.getFields())
        {
        		for(MetadataFieldValue value : fieldGroup.getValues())
        		{
        			if(value.getValue() != null && !value.getValue().equals(""))
        			{
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
	
	private String generateBitstreamPostXML(String bundleName, Boolean isPrimary) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        org.w3c.dom.Document domDoc = docBuilder.newDocument();
        Element rootElement = domDoc.createElement("bitstream");
        domDoc.appendChild(rootElement);
        
        
        
        StringWriter stw = new StringWriter(); 
        Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
        serializer.transform(new DOMSource(domDoc), new StreamResult(stw)); 
		
        return stw.toString();
	}
	
	private String generatePolicyPostXML(String bitstreamId, String groupName) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        org.w3c.dom.Document domDoc = docBuilder.newDocument();
        Element rootElement = domDoc.createElement("policy");
        domDoc.appendChild(rootElement);
        
        Element actionElement = domDoc.createElement("action");
        actionElement.setTextContent("READ");
        rootElement.appendChild(actionElement);
        
        Element epersonIdElement = domDoc.createElement("epersonId");
        epersonIdElement.setTextContent("-1");
        rootElement.appendChild(epersonIdElement);
        
        Element groupIdElement = domDoc.createElement("groupId");
        groupIdElement.setTextContent(groupName);
        rootElement.appendChild(groupIdElement);
        
        Element resourceIdElement = domDoc.createElement("resourceId");
        resourceIdElement.setTextContent(bitstreamId);
        rootElement.appendChild(resourceIdElement);
        
        Element resourceTypeElement = domDoc.createElement("resourceType");
        resourceTypeElement.setTextContent("bitstream");
        rootElement.appendChild(resourceTypeElement);
        
        Element rpTypeElement = domDoc.createElement("rpType");
        rpTypeElement.setTextContent("TYPE_CUSTOM");
        rootElement.appendChild(rpTypeElement);
        
        
        StringWriter stw = new StringWriter(); 
        Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
        serializer.transform(new DOMSource(domDoc), new StreamResult(stw)); 
		
        return stw.toString();

	}
}
