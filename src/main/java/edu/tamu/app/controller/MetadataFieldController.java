/* 
 * MetadataFieldController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;

/** 
 * Metadata Field Controller
 * 
 * @author
 *
 */
@Component
@RestController
@MessageMapping("/metadata")
public class MetadataFieldController {
	
	@Value("${app.mount}") 
   	private String mount;
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private ProjectLabelProfileRepo projectFieldProfileRepo;
	
	@Autowired
	private MetadataFieldGroupRepo metadataFieldGroupRepo;
	
	@Autowired
	ApplicationContext appContext;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * Endpoint to return list of projects.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/projects")
	@Auth
	@SendToUser
	public ApiResponse getProjects(Message<?> message, @ReqId String requestId) throws Exception {
				
		String directory = "";
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/projects/";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> projects = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
            	projects.add(path.getFileName().toString());            
            }
        } catch (IOException ex) {
        	System.out.println("Could not create directory stream!! No projects added!");
        }

		return new ApiResponse("success", projects, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return metadata headers for given project.
	 * 
	 * @param 		message			Message<?>
	 * @param 		project			@DestinationVariable String
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/headers/{project}")
	@Auth
	@SendToUser
	public ApiResponse getMetadataHeaders(Message<?> message, @DestinationVariable String project, @ReqId String requestId) throws Exception {
		
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/metadata.json")));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		Map<String, Object> metadataMap = null;
		
		try {
			metadataMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> metadataHeaders = new ArrayList<String>();
		
		for (String key : metadataMap.keySet()) {
			
			if(key.equals(project)) {				
				@SuppressWarnings("unchecked")
				List<Map<String, String>> fields = (List<Map<String, String>>) metadataMap.get(key);
				
				for (Map<String, String> field : fields) {
					metadataHeaders.add(field.get("label"));
				}				
			}
			
		}
		
		if(metadataHeaders.isEmpty()) {
			@SuppressWarnings("unchecked")
			List<Map<String, String>> fields = (List<Map<String, String>>) metadataMap.get("default");
			
			for (Map<String, String> field : fields) {
				metadataHeaders.add(field.get("label"));
			}
		}
		
		metadataHeaders.add("BUNDLE:ORIGINAL");
		
		Collections.sort(metadataHeaders);
						
		return new ApiResponse("success", metadataHeaders, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return all published metadata fields as dspace csv by project.
	 * 
	 * @param 		message			Message<?>
	 * @param 		project			@DestinationVariable String
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/csv/{project}")
	@Auth
	@SendToUser
	public ApiResponse getCSVByroject(Message<?> message, @DestinationVariable String project, @ReqId String requestId) throws Exception {
		
		List<List<String>> metadata = new ArrayList<List<String>>();
		
		projectRepo.findByName(project).getDocuments().stream().filter(isAccepted()).collect(Collectors.<Document>toList()).forEach(document -> {
			
			Set<MetadataFieldGroup> metadataFields = new TreeSet<MetadataFieldGroup>(document.getFields());
			
			List<String> documentMetadata = new ArrayList<String>();
			
			documentMetadata.add(document.getName() + ".pdf");
			
			metadataFields.forEach(field -> {
				String values = null;
				boolean firstPass = true;
				for(MetadataFieldValue medataFieldValue : field.getValues()) {					
					if(firstPass) {
						values = medataFieldValue.getValue();
						firstPass = false;
					}
					else {
						values += "||" +  medataFieldValue.getValue();
					}					
				}
				documentMetadata.add(values);
			});
						
			metadata.add(documentMetadata);
			
		});
		
		return new ApiResponse("success", metadata, new RequestId(requestId));
	}
	
	/**
	 * Websocket endpoint to request credentials.
	 * 
	 * @param 		shibObj			@Shib Object
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/saf/{project}")
	@SendToUser
	@Auth
	public ApiResponse saf(@Shib Object shibObj, @DestinationVariable String project, @ReqId String requestId) throws Exception {
		
		System.out.println("Generating SAF for project " + project);
		
		//for each published document
		List<Document> documents = projectRepo.findByName(project).getDocuments().stream().filter(isAccepted()).collect(Collectors.<Document>toList());
		
		//TODO:  get straight on where we want to write this bad boy
		
		String directory = "";
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/exports/";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String archiveDirectoryName = directory + project + System.currentTimeMillis();
		
		System.out.println(archiveDirectoryName);
		
		if(documents.size() > 0) {
			//make a containing directory for the SAF			
			File safDirectory = new File(archiveDirectoryName);
			safDirectory.mkdir();
		}
		
		for(Document document: documents) {
			
			System.out.println("Writing archive for document " + document.getName());
			
			//create a directory
			File itemDirectory = new File(archiveDirectoryName + "/" + document.getName());
			itemDirectory.mkdir();
			
			//copy the content files to the directory
			File pdf = document.pdf();
			
			// TODO: do something with File txt or remove it
			
			@SuppressWarnings("unused")
			File txt = document.txt();
 			
 			try {
			    FileUtils.copyDirectory(pdf.getParentFile(), itemDirectory);
			} catch (IOException e) {
			    e.printStackTrace();
			}
 			
 			PrintStream license = new PrintStream(itemDirectory+"/license.txt");
 			license.print("The materials in this collection are hereby licensed.");
 			license.flush();
 			license.close();
 			
 			PrintStream manifest = new PrintStream(itemDirectory+"/contents");
 			manifest.print(pdf.getName()+"\tbundle:ORIGINAL\tprimary:true\tpermissions:-r 'member'\nlicense.txt\tbundle:LICENSE");
 			manifest.flush();
 			manifest.close();
 			
 			
 			//for each schema in the metadata
 			Map <String, PrintStream> schemaToFile = new HashMap<String, PrintStream>();
 			
 			Set<MetadataFieldGroup> metadatafields = document.getFields();
 			
 			for(MetadataFieldGroup metadataField : metadatafields) {
 	 			//write a dublin-core style xml file
 				String label = metadataField.getLabel().getName();
 				String schema = label.split("\\.")[0];
 				//System.out.println("Got schema " + schema);
 				String element = label.split("\\.")[1];
 				//System.out.println("Got element "+ element);
 				String qualifier = null;
 				if(label.split("\\.").length > 2) {
 					qualifier = label.split("\\.")[2];
 				}	
 				
 				if(!schemaToFile.containsKey(schema)) {
 					String filename = schema.equals("dc") ? "dublin_core.xml" : "metadata_" + schema + ".xml";
 					schemaToFile.put(schema, new PrintStream(itemDirectory + "/" + filename));
 					schemaToFile.get(schema).print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dublin_core schema=\"" + schema + "\">"); 	 				
 				}
 				
 				for(MetadataFieldValue metadataFieldValue : metadataField.getValues()) {
 					if(metadataFieldValue.getValue().equals("")) continue;
 					
 					schemaToFile.get(schema).print("<dcvalue element=\"" + element+"\" " + 
 												   ( qualifier!=null? "qualifier=\"" + qualifier + "\"" : "" ) +
 												   ">" + escapeForXML(metadataFieldValue.getValue()) + "</dcvalue>");
 				}
 			}
 			
 			for(PrintStream printStream : schemaToFile.values()) {
				printStream.print("</dublin_core>");
				printStream.close();
			}
 			
		}
		
		return new ApiResponse("success", "Your SAF has been written to the server filesystem.", new RequestId(requestId));
	}

	private String escapeForXML(String value) {
		value = value.replace("&", "&amp;");
		value = value.replace("\"", "&quot;");
		value = value.replace("'", "&apos;");
		value = value.replace("<", "&lt;");
		value = value.replace(">", "&gt;");
		return value;
	}
	
	/**
	 * Endpoint to return all by status metadata fields.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/status/{status}")
	@Auth
	@SendToUser
	public ApiResponse published(Message<?> message, @DestinationVariable String status, @ReqId String requestId) throws Exception {
		
		List<List<String>> metadata = new ArrayList<List<String>>();
		
		documentRepo.findByStatus(status).forEach(document -> {
					
			new TreeSet<MetadataFieldGroup>(document.getFields()).forEach(field -> {
				
				field.getValues().forEach(value -> {
					
					List<String> documentMetadata = new ArrayList<String>();
					
					documentMetadata.add(field.getLabel().getName());
					documentMetadata.add(value.getValue());
					
					metadata.add(documentMetadata);
					
				});
				
			});
			
		});
		
		return new ApiResponse("success", metadata, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return all metadata fields.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/all")
	@Auth
	@SendToUser
	public ApiResponse all(Message<?> message, @ReqId String requestId) throws Exception {		
		Map<String, List<MetadataFieldGroup>> metadataMap = new HashMap<String, List<MetadataFieldGroup>>();
		metadataMap.put("list", metadataFieldGroupRepo.findAll());		
		return new ApiResponse("success", metadataMap, new RequestId(requestId));
	}
	
	public static Predicate<Document> isPublished() {		
	    return d -> d.getStatus().equals("Published");
	}
	
	public static Predicate<Document> isAccepted() {		
	    return d -> d.getStatus().equals("Accepted");
	}
	
	public static Predicate<Document> isPending() {		
	    return d -> d.getStatus().equals("Pending");
	}
	
}
