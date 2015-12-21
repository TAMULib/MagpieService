package edu.tamu.app.service;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.framework.model.ApiResponse;

@Service
public class DocumentPushService 
{
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private DocumentRepo documentRepo;
	
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
	
	
	
	
	
	public ApiResponse push(Document document)
	{
		try {
		    
			String itemName = document.getName();
			
			
			URL createItemUrl = new URL(repoUrl+"/rest/collections/"+collectionId+"/items");
						
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
			
	        String xml = stw.toString();
	        
	        
	        HttpURLConnection connection = (HttpURLConnection) createItemUrl.openConnection();
			
			connection.setRequestMethod("POST");			
			connection.setRequestProperty("Accept", "application/json");			
			connection.setRequestProperty("Content-Type", "application/xml");
			
			connection.setRequestProperty("Content-Length",  String.valueOf(xml.length()));
			
			String token = authenticateRest(username, password);
			connection.setRequestProperty("rest-dspace-token", token);
			
			connection.setDoOutput(true);
			
			// Write data
	        OutputStream os = connection.getOutputStream();
	        os.write(xml.getBytes());
					
			// Read response
	        StringBuilder response = new StringBuilder();
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	          
	        String line;
	        while ( (line = br.readLine()) != null) {
	            response.append(line);
	        }
	                 
	        // Close streams
	        br.close();
	        os.close();
	        
	        System.out.println("And here is your response:\n\n" + response.toString()+"\n");
	        
	        //parse response to get new handle
	        JsonNode responseNode = objectMapper.readTree(response.toString());
	        String handleString = responseNode.get("handle").asText();
	        
	        //add new handle to document, change it's status to published, save it
	        String publishedUriString = repoUrl + "/" + defaultRepoUIPath + "/" + handleString;
	        document.setPublishedUriString(publishedUriString);
	        
	        document.setStatus("Published");
	        
	        document = documentRepo.save(document);
	        
	        //return the document in an ApiResponse
			return new ApiResponse(SUCCESS,"New item created at URI: " + publishedUriString, document);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return new ApiResponse(ERROR,"Failed to create items {" + e.getMessage() + "}" , document);
		}
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

}
