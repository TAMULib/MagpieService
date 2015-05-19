/* 
 * DocumentController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.RequestId;
import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.impl.MetadataFieldImpl;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.response.marc.FlatMARC;
import edu.tamu.app.service.VoyagerService;

/** 
 * Document Controller
 * 
 * @author
 *
 */
@Component
@RestController
@MessageMapping("/metadata")
public class MetadataFieldController {
	
	@Autowired
	private DocumentRepo docRepo;
	
	@Autowired
	private MetadataFieldRepo metadataRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired 
	private VoyagerService voyagerService; 
	
	/**
	 * Endpoint to return all metadata fields.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/all")
	@SendToUser
	public ApiResImpl all(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		Map<String, List<MetadataFieldImpl>> metadataMap = new HashMap<String, List<MetadataFieldImpl>>();
		metadataMap.put("list", metadataRepo.findAll());
		return new ApiResImpl("success", metadataMap, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return all published metadata fields.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/published")
	@SendToUser
	public ApiResImpl published(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		
		List<List<String>> metadata = new ArrayList<List<String>>();
		
		List<DocumentImpl> documents = docRepo.getAllByStatus("Published");
		
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
	 * Endpoint to clearn all metadata fields for name.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/clear")
	@SendToUser
	public ApiResImpl clear(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
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
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 */
	@MessageMapping("/get")
	@SendToUser
	public ApiResImpl get(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
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
		
		metadataMap.put("dc.creator", flatMarc.getCreator());
		metadataMap.put("dc.title", flatMarc.getTitle());
		metadataMap.put("dc.date.created", flatMarc.getDateCreated());
		metadataMap.put("dc.date.issued", flatMarc.getDateIssued());
		metadataMap.put("dc.subject.lcsh", flatMarc.getSubjectIcsh());
		metadataMap.put("dc.subject", flatMarc.getSubject());
		metadataMap.put("dc.description", flatMarc.getDescription());
		metadataMap.put("dc.description.abstract", flatMarc.getDescriptionAbstract());
		metadataMap.put("dc.degree.grantor", flatMarc.getDegreeGrantor());
		
		for (MetadataFieldImpl field : fields) {			
			metadataMap.put(field.getLabel(), field.getValues());
		}
		
		return new ApiResImpl("success", metadataMap, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to add new metadata field to a document.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@SuppressWarnings("unchecked")
	@MessageMapping("/add")
	@SendToUser
	public ApiResImpl add(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,Object>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String, Object> metadataFields = (Map<String, Object>) map.get("metadata") ;
		
		for (String label : metadataFields.keySet()) {
						
			List<String> metadataEntry = new ArrayList<String>();
			
			List<String> metadataValues = (List<String>) metadataFields.get(label);
			
			for (String metadata :metadataValues) {
				metadataEntry.add(metadata);
			}
			
			MetadataFieldImpl documentMetadata = new MetadataFieldImpl((String) map.get("name"), label, metadataEntry);
			metadataRepo.save(documentMetadata);

		}
		
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}
		
}
