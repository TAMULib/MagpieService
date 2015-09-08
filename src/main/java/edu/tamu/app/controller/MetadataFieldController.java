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

import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.ProjectFieldProfileRepo;

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
	private DocumentRepo documentRepo;
	
	@Autowired
	private ProjectFieldProfileRepo projectFieldProfileRepo;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;
	
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
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> projects = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
            	projects.add(path.getFileName().toString());            
            }
        } catch (IOException ex) {}

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
	@SuppressWarnings("null")
	@MessageMapping("/csv/{project}")
	@Auth
	@SendToUser
	public ApiResponse getCSVByroject(Message<?> message, @DestinationVariable String project, @ReqId String requestId) throws Exception {
		
		List<Document> documents = documentRepo.findByStatusAndProject("Published", project);
		
		List<List<String>> metadata = new ArrayList<List<String>>();
		
		for(Document document : documents) {
			
			List<MetadataField> metadataFields = document.getFields();
			
			List<String> documentMetadata = new ArrayList<String>();
			
			documentMetadata.add(document.getName() + ".pdf");
			
			Collections.sort(metadataFields, new LabelComparator());
						
			for(MetadataField metadatum : metadataFields) {
				
				String values = null;
				for(MetadataFieldValue medataFieldValue : metadatum.getValues()) {					
					if(medataFieldValue == null) {
						values = medataFieldValue.getValue();
					}
					else {
						values += "||" +  medataFieldValue.getValue();
					}					
				}
				documentMetadata.add(values);

			}
			
			metadata.add(documentMetadata);
			
		}
		
		return new ApiResponse("success", metadata, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return all published metadata fields.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/published")
	@Auth
	@SendToUser
	public ApiResponse published(Message<?> message, @ReqId String requestId) throws Exception {
		List<List<String>> metadata = new ArrayList<List<String>>();
		
		List<Document> documents = documentRepo.findByStatus("Published");
		
		for(Document document : documents) {
			
			List<MetadataField> metadataFields = document.getFields();
			
			for(MetadataField metadataField : metadataFields) {
				
				for(MetadataFieldValue metadataFieldValue : metadataField.getValues()) {
					
					List<String> documentMetadata = new ArrayList<String>();
					
					documentMetadata.add(metadataField.getLabel().getName());
					documentMetadata.add(metadataFieldValue.getValue());
					
					metadata.add(documentMetadata);
				}
				
			}
			
		}
		
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
		Map<String, List<MetadataField>> metadataMap = new HashMap<String, List<MetadataField>>();
		metadataMap.put("list", metadataFieldRepo.findAll());		
		return new ApiResponse("success", metadataMap, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to clearn all metadata fields for name.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/clear")
	@Auth
	@SendToUser
	public ApiResponse clear(Message<?> message, @ReqId String requestId) throws Exception {
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String data = accessor.getNativeHeader("data").get(0).toString();
		
		Map<String,String> map = new HashMap<String,String>();
		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		int removed = 0;
		for(MetadataField field : documentRepo.findByName(map.get("name")).getFields()) {
			metadataFieldRepo.delete(field);
			removed++;
		}
		
		return new ApiResponse("success", "removed " + removed, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to add new metadata field to a document.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */	
	@MessageMapping("/add")
	@Auth
	@SendToUser
	public ApiResponse add(Message<?> message, @ReqId String requestId) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,Object>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Object> metadataFields = (Map<String, Object>) map.get("metadata") ;
		
		for (String label : metadataFields.keySet()) {
			
			@SuppressWarnings("unchecked")
			List<String> metadataValues = (List<String>) metadataFields.get(label);
			
			Document document = documentRepo.findByName((String) map.get("name"));
						
			MetadataField metadataField = null;
					
			for(MetadataField field : document.getFields()) {
				if(field.getLabel().getName().equals(label)) {
					metadataField = field;
				}
			}
						
			for (String value : metadataValues) {
				metadataField.addValue(new MetadataFieldValue(value, metadataField));
			}
			
			document.addField(metadataField);
			
			documentRepo.save(document);
		}
		
		return new ApiResponse("success", "ok", new RequestId(requestId));
	}
	
	/**
	 * Class for comparing MetadataFieldImpl by label.
	 * 
	 * @author
	 *
	 */
	class LabelComparator implements Comparator<MetadataField>
	{
		/**
		 * Compare labels of MetadataFieldImpl
		 * 
		 * @param		mfi1		MetadataFieldImpl
		 * @param		mfi2		MetadataFieldImpl
		 * 
		 * @return		int
		 */
		@Override
		public int compare(MetadataField m1, MetadataField m2) {
			return m1.getLabel().getName().compareTo(m2.getLabel().getName());
		}
	}
		
}
