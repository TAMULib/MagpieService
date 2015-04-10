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
import edu.tamu.app.model.impl.MetadataFieldImpl;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;

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
		System.out.println(metadataRepo.findAll().size());
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
		Map<String, List<MetadataFieldImpl>> metadataMap = new HashMap<String, List<MetadataFieldImpl>>();
		metadataMap.put("list", metadataRepo.getMetadataFieldsByStatus("Published"));
		return new ApiResImpl("success", metadataMap, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return metadata fileds by filename.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 */
	@SuppressWarnings("unchecked")
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
		
		List<MetadataFieldImpl> fields = metadataRepo.getMetadataFieldsByFilename(headerMap.get("filename"));
		
		Map<String, Object> metadataMap = new HashMap<String, Object>();
		
		for (MetadataFieldImpl field : fields) {
			if(field.getIsRepeatable()) {
				
				Map<String, String> map = new HashMap<String, String>();
				
				if(metadataMap.containsKey(field.getLabel())) {
					map = (Map<String, String>) metadataMap.get(field.getLabel());
				}
				
				map.put(String.valueOf(field.getIndex()), field.getValue());
				
				metadataMap.put(field.getLabel(), map);
			}
			else {
				metadataMap.put(field.getLabel(), field.getValue());
			}
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
	@MessageMapping("/add")
	@SendToUser
	public ApiResImpl add(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> map = new HashMap<String,String>();
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		MetadataFieldImpl metadata = null;
		
		if(Boolean.parseBoolean(map.get("isRepeatable"))) {
			
			List<MetadataFieldImpl> fields = metadataRepo.getMetadataFieldsByFilename(map.get("filename"));

			for (MetadataFieldImpl field : fields) {
				
				if(field.getLabel().equals(map.get("label"))) {
					
					if(map.get("value").equals(field.getValue())) {
						metadata = field;
					}
					else {
						if(field.getIndex() == Integer.parseInt(map.get("index"))) {
							metadata = field;
							metadata.setValue(map.get("value"));
						}
					}
				}
			}
			
		}
		else {
			List<MetadataFieldImpl> fields = metadataRepo.getMetadataFieldsByFilename(map.get("filename"));

			for (MetadataFieldImpl field : fields) {
				
				if(field.getLabel().equals(map.get("label"))) {
					metadata = field;
					metadata.setValue(map.get("value"));					
				}
				
			}
		}
		
		if(metadata == null) {
			metadata = new MetadataFieldImpl(map.get("filename"), 
					   map.get("label"), 
					   map.get("value"),
					   Boolean.parseBoolean(map.get("isRepeatable")),
					   Integer.parseInt(map.get("index")),
					   map.get("status"));
		}
		
		metadataRepo.save(metadata);
		
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}
	
	/**
	 * Endpoint to publish metadata field to a document.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/publish")
	@SendToUser
	public ApiResImpl publish(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> map = new HashMap<String,String>();
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		List<MetadataFieldImpl> fields = metadataRepo.getMetadataFieldsByFilename(map.get("filename"));

		for (MetadataFieldImpl field : fields) {
			
			field.setStatus("Published");
			metadataRepo.save(field);
		}
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}
		
}
