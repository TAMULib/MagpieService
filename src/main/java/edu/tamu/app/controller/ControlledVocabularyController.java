/* 
 * ControlledVocabularyController.java 
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
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;

/** 
 * Document Controller
 * 
 * @author
 *
 */
@Component
@RestController
@MessageMapping("/cv")
public class ControlledVocabularyController {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private static final Logger logger = Logger.getLogger(ControlledVocabularyController.class);
	
	/**
	 * Get all controller vocabulary.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/all")
	@Auth
	@SendToUser
	public ApiResponse getAllControlledVocabulary(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/cv.json")));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		Map<String, Object> cvMap = null;
		
		try {
			cvMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			logger.error("Error reading cv json",e);
		}
		
		return new ApiResponse("success", cvMap, new RequestId(requestId));
	}

	/**
	 * Get controlled vocabulary by label.
	 * 
	 * @param 		message			Message<?>
	 * @param 		label			@DestinationVariable String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/{label}")
	@Auth
	@SendToUser
	public ApiResponse getControlledVocabularyByField(Message<?> message, @DestinationVariable String label) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/cv.json")));
		} catch (IOException e2) {
			logger.error("Error reading cv json",e2);
		}
		
		Map<String, Object> cvMap = null;
		
		try {
			cvMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			logger.error("Error reading cv json value",e);
		}
		
		return new ApiResponse("success", cvMap.get(label), new RequestId(requestId));
	}
		
}
