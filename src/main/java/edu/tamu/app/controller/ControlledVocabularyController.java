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

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/** 
 * Document Controller
 * 
 * @author
 *
 */
@Controller
@ApiMapping("/cv")
public class ControlledVocabularyController {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private static final Logger logger = Logger.getLogger(ControlledVocabularyController.class);
	
	/**
	 * Get all controller vocabulary.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@ApiMapping("/all")
	@Auth
	public ApiResponse getAllControlledVocabulary(Message<?> message) throws Exception {
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
			return new ApiResponse(ERROR, "Error reading cv json");
		}
		
		return new ApiResponse(SUCCESS, cvMap);
	}

	/**
	 * Get controlled vocabulary by label.
	 * 
	 * @param 		message			Message<?>
	 * @param 		label			@DestinationVariable String
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@ApiMapping("/{label}")
	@Auth
	public ApiResponse getControlledVocabularyByField(Message<?> message, @ApiVariable String label) throws Exception {
		URL location = this.getClass().getResource("/config"); 
		String fullPath = location.getPath();
		
		String json = null;
		
		try {
			json = new String(readAllBytes(get(fullPath + "/cv.json")));
		} catch (IOException e2) {
			logger.error("Error reading cv json",e2);
			return new ApiResponse(ERROR, "Error reading cv json");
		}
		
		Map<String, Object> cvMap = null;
		
		try {
			cvMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (IOException e) {
			logger.error("Error reading cv json value",e);
			return new ApiResponse(ERROR, "Error reading cv json value");
		}
		
		return new ApiResponse(SUCCESS, cvMap.get(label));
	}
		
}
