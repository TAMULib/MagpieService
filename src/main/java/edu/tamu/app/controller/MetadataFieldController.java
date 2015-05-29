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
import java.lang.reflect.Field;
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
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.ApplicationContextProvider;
import edu.tamu.app.aspect.annotation.ReqId;
import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.impl.MetadataFieldImpl;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.response.marc.FlatMARC;
import edu.tamu.app.service.VoyagerService;

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
	private DocumentRepo docRepo;
	
	@Autowired
	private MetadataFieldRepo metadataRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired 
	private VoyagerService voyagerService; 
	
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
	@SendToUser
	public ApiResImpl getProjects(Message<?> message, @ReqId String requestId) throws Exception {
				
		String directory = "";
		try {
			directory = ApplicationContextProvider.appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<String> projects = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
            	projects.add(path.getFileName().toString());            
            }
        } catch (IOException ex) {}

		return new ApiResImpl("success", projects, new RequestId(requestId));
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
	@SendToUser
	public ApiResImpl getMetadataHeaders(Message<?> message, @DestinationVariable String project, @ReqId String requestId) throws Exception {
		
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
						
		return new ApiResImpl("success", metadataHeaders, new RequestId(requestId));
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
	@SendToUser
	public ApiResImpl getCSVByroject(Message<?> message, @DestinationVariable String project, @ReqId String requestId) throws Exception {
		
		List<DocumentImpl> documents = docRepo.findByStatusAndProject("Published", project);
		
		List<List<String>> metadata = new ArrayList<List<String>>();
		
		for(DocumentImpl document : documents) {
			
			List<MetadataFieldImpl> metadataList = metadataRepo.getMetadataFieldsByName(document.getName());
			
			List<String> documentMetadata = new ArrayList<String>();
			
			documentMetadata.add(document.getName() + ".pdf");
			
			Collections.sort(metadataList, new LabelComparator());
			
			for(MetadataFieldImpl metadataField : metadataList) {
				
				String values = null;
				for(String value : metadataField.getValues()) {					
					if(values == null) {
						values = value;
					}
					else {
						values += "||" + value;
					}					
				}
				documentMetadata.add(values);

			}
			
			metadata.add(documentMetadata);
			
		}
		
		return new ApiResImpl("success", metadata, new RequestId(requestId));
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
	@SendToUser
	public ApiResImpl published(Message<?> message, @ReqId String requestId) throws Exception {
		List<List<String>> metadata = new ArrayList<List<String>>();
		
		List<DocumentImpl> documents = docRepo.findByStatus("Published");
		
		for(DocumentImpl document : documents) {
			
			List<MetadataFieldImpl> metadataFields = metadataRepo.getMetadataFieldsByName(document.getName());
			
			for(MetadataFieldImpl metadataField : metadataFields) {
				
				for(String value : metadataField.getValues()) {
					
					List<String> documentMetadata = new ArrayList<String>();
					documentMetadata.add(metadataField.getName());
					documentMetadata.add(metadataField.getLabel());
					documentMetadata.add(value);
					
					metadata.add(documentMetadata);
				}
				
			}
			
		}
		
		return new ApiResImpl("success", metadata, new RequestId(requestId));
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
	@SendToUser
	public ApiResImpl all(Message<?> message, @ReqId String requestId) throws Exception {
		Map<String, List<MetadataFieldImpl>> metadataMap = new HashMap<String, List<MetadataFieldImpl>>();
		metadataMap.put("list", metadataRepo.findAll());
		return new ApiResImpl("success", metadataMap, new RequestId(requestId));
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
	@SendToUser
	public ApiResImpl clear(Message<?> message, @ReqId String requestId) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String data = accessor.getNativeHeader("data").get(0).toString();
		Map<String,String> map = new HashMap<String,String>();
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		Long removed = metadataRepo.deleteByName(map.get("name"));		
		return new ApiResImpl("success", "removed " + removed, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return metadata fields by name.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 */
	@SuppressWarnings("unchecked")
	@MessageMapping("/get")
	@SendToUser
	public ApiResImpl getMetadata(Message<?> message, @ReqId String requestId) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String data = accessor.getNativeHeader("data").get(0).toString();
		Map<String, String> headerMap = new HashMap<String, String>();
		
		try {
			headerMap = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<MetadataFieldImpl> fields = metadataRepo.getMetadataFieldsByName(headerMap.get("name"));
		
		Map<String, Object> metadataMap = new HashMap<String, Object>();
		
		FlatMARC flatMarc = new FlatMARC(voyagerService.getMARC(headerMap.get("name")));
		
		Field[] marcFields = FlatMARC.class.getDeclaredFields();
		for (Field field : marcFields) {
			field.setAccessible(true);
            List<String> marcList = new ArrayList<String>();
            if(field.getGenericType().toString().equals("java.util.List<java.lang.String>")) {
            	for(String string : (List<String>) field.get(flatMarc)) {
            		marcList.add(string);
            	}
            }
            else {
            	marcList.add(field.get(flatMarc).toString());
            }            
            metadataMap.put(field.getName().replace('_','.'), marcList);
        }
		
		for (MetadataFieldImpl field : fields) {			
			metadataMap.put(field.getLabel(), field.getValues());
		}
		
		return new ApiResImpl("success", metadataMap, new RequestId(requestId));
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
	@SendToUser
	public ApiResImpl add(Message<?> message, @ReqId String requestId) throws Exception {		
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
						
			List<String> metadataEntry = new ArrayList<String>();
			@SuppressWarnings("unchecked")
			List<String> metadataValues = (List<String>) metadataFields.get(label);
			
			for (String metadata :metadataValues) {
				metadataEntry.add(metadata);
			}
			
			MetadataFieldImpl documentMetadata = new MetadataFieldImpl((String) map.get("name"), label, metadataEntry);
			metadataRepo.save(documentMetadata);

		}
		
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}
	
	/**
	 * Class for comparing MetadataFieldImpl by label.
	 * 
	 * @author
	 *
	 */
	class LabelComparator implements Comparator<MetadataFieldImpl>
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
		public int compare(MetadataFieldImpl mfi1, MetadataFieldImpl mfi2) {
			return mfi1.getLabel().compareTo(mfi2.getLabel());
		}
	}
		
}
