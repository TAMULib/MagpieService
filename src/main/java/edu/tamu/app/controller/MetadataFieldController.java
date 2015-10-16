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

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectMinimal;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;

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
	private MetadataFieldGroupRepo metadataFieldGroupRepo;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 
	
	private static final Logger logger = Logger.getLogger(MetadataFieldController.class);

	
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
	public ApiResponse getProjects(Message<?> message) throws Exception {
		List<ProjectMinimal> projects = new ArrayList<>();
		projects = projectRepo.findAllAsObject();
		return new ApiResponse(SUCCESS, projects);
	}
	
	/**
	 * Endpoint to unlock a given project
	 * 
	 * @param 		message			Message<?>
	 * @param 		projectToUnlock	@DestinationVariable String
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/unlock/{projectToUnlock}")
	@Auth
	@SendToUser
	public ApiResponse unlockProject(Message<?> message, @DestinationVariable String projectToUnlock) throws Exception {
		Project project = projectRepo.findByName(projectToUnlock);
		project.setIsLocked(false);;
		projectRepo.save(project);
		return new ApiResponse(SUCCESS);
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
	public ApiResponse getMetadataHeaders(Message<?> message, @DestinationVariable String project) throws Exception {
		
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/metadata.json")));
		} catch (IOException e2) {
			logger.error("Error reading metadata json",e2);
		}
		
		Map<String, Object> metadataMap = null;
		
		try {
			metadataMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			logger.error("Error reading metadata json value",e);
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
						
		return new ApiResponse(SUCCESS, metadataHeaders);
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
	public ApiResponse getCSVByroject(Message<?> message, @DestinationVariable String project) throws Exception {
		
		List<List<String>> metadata = new ArrayList<List<String>>();
		
		projectRepo.findByName(project).getDocuments().stream().filter(isAccepted()).collect(Collectors.<Document>toList()).forEach(document -> {
			
			Set<MetadataFieldGroup> metadataFields = document.getFields();
			
			List<MetadataFieldGroup> metadataFieldsList = new ArrayList<MetadataFieldGroup>();
			
			metadataFieldsList.addAll(metadataFields);
			
			Collections.sort(metadataFieldsList, new LabelComparator());
			
			List<String> documentMetadata = new ArrayList<String>();
			
			documentMetadata.add(document.getName() + ".pdf");
			
			metadataFieldsList.forEach(field -> {
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
		
		return new ApiResponse(SUCCESS, metadata);
	}
	
	/**
	 * Websocket endpoint to export saf.
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
	public ApiResponse saf(@Shib Object shibObj, @DestinationVariable String project) throws Exception {
		
		System.out.println("Generating SAF for project " + project);
		//for each published document

		Project exportableProject = projectRepo.findByName(project);
		List<Document> documents = exportableProject.getDocuments().stream().filter(isAccepted()).collect(Collectors.<Document>toList());
		
		String directory = "";
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/exports/";
		} catch (IOException e) {
			logger.error("Error building exports directory",e);
		}
		
		String archiveDirectoryName = directory + project + System.currentTimeMillis();
		
		if (logger.isDebugEnabled()) {
			logger.debug("Archive Directory: "+archiveDirectoryName);
		}
		
		if(documents.size() > 0) {
			//make a containing directory for the SAF			
			File safDirectory = new File(archiveDirectoryName);
			safDirectory.mkdir();
		}
		
		for(Document document: documents) {
			
			if (logger.isDebugEnabled()) {
				logger.debug("Writing archive for document " + document.getName());
			}
			
			//create a directory
			File itemDirectory = new File(archiveDirectoryName + "/" + document.getName());
			itemDirectory.mkdir();
				
			File originDir = null;
 			try {
 				String documentDirectory = appContext.getResource("classpath:static" + document.getPdfPath()).getFile().getAbsolutePath();
 				documentDirectory = documentDirectory.substring(0, documentDirectory.length() - (document.getName().length() + 5));
 				originDir = new File(documentDirectory);
			    FileUtils.copyDirectory(originDir, itemDirectory);
			} catch (IOException e) {	
			    logger.error("Error copying document directory",e);
			}
 			
 			PrintStream license = new PrintStream(itemDirectory+"/license.txt");
 			license.print("The materials in this collection are hereby licensed.");
 			license.flush();
 			license.close();
 			
 			PrintStream manifest = new PrintStream(itemDirectory+"/contents");
 			for (File file : originDir.listFiles()) {
 	 			manifest.print(file.getName() + "\tbundle:ORIGINAL\tprimary:true\tpermissions:-r 'member'\n");
 			}
 			manifest.print("license.txt\tbundle:LICENSE");
 			manifest.flush();
 			manifest.close();
 			
 			
 			//for each schema in the metadata
 			Map <String, PrintStream> schemaToFile = new HashMap<String, PrintStream>();
 			
 			Set<MetadataFieldGroup> metadataFields = document.getFields();
 			
 			List<MetadataFieldGroup> metadataFieldsList = new ArrayList<MetadataFieldGroup>();
			
			metadataFieldsList.addAll(metadataFields);
			
			Collections.sort(metadataFieldsList, new LabelComparator());
 			
 			for(MetadataFieldGroup metadataField : metadataFieldsList) {
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
 			document.setStatus("Pending");
 			document = documentRepo.save(document);
 			
 			Map<String, Object> documentMap = new HashMap<String, Object>();
 			
 			documentMap.put("document", document);
 			documentMap.put("isNew", "false");
 			
 			simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse(SUCCESS, documentMap));
		}
		exportableProject.setIsLocked(true);
		projectRepo.save(exportableProject);
		return new ApiResponse(SUCCESS, "Your SAF has been written to the server filesystem.");
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
	public ApiResponse published(Message<?> message, @DestinationVariable String status) throws Exception {
		
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
		
		return new ApiResponse(SUCCESS, metadata);
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
	public ApiResponse all(Message<?> message) throws Exception {		
		Map<String, List<MetadataFieldGroup>> metadataMap = new HashMap<String, List<MetadataFieldGroup>>();
		metadataMap.put("list", metadataFieldGroupRepo.findAll());		
		return new ApiResponse(SUCCESS, metadataMap);
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
	
	/**
	 * Class for comparing MetadataFieldImpl by label.
	 * 
	 * @author
	 *
	 */
	class LabelComparator implements Comparator<MetadataFieldGroup>
	{
		/**
		 * Compare labels of MetadataFieldImpl
		 * 
		 * @param		mfg1		MetadataFieldGroup
		 * @param		mfg2		MetadataFieldGroup
		 * 
		 * @return		int
		 */
		@Override
		public int compare(MetadataFieldGroup mfg1, MetadataFieldGroup mfg2) {
			return mfg1.getLabel().getName().compareTo(mfg2.getLabel().getName());
		}
	}
	
}
