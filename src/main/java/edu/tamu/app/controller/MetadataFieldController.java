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
	 * 
	 * @param filename
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@MessageMapping("/get")
	@SendToUser
	public ApiResImpl get(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> map = new HashMap<String,String>();
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String document = map.get("filename");
		
		
		
		return new ApiResImpl("success", "", new RequestId(requestId));
	}
	
	/**
	 * 
	 * @param filename
	 * @param message
	 * @return
	 * @throws Exception
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
					   Integer.parseInt(map.get("index")));
		}
		
		metadataRepo.save(metadata);
		
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}
	
}
