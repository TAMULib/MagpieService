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

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.RequestId;

/** 
 * Document Controller
 * 
 * @author
 *
 */
@Component
@RestController
@MessageMapping("/document")
public class DocumentController {

	@Value("${app.directory}") 
	private String directory;
	
	@Autowired
	private DocumentRepo docRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@MessageMapping("/all")
	@SendToUser
	public ApiResImpl allDocuments(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		Map<String,List<DocumentImpl>> map = new HashMap<String,List<DocumentImpl>>();
		map.put("list", docRepo.findAll());
		return new ApiResImpl("success", map, new RequestId(requestId));
	}
	
	/**
	 * 
	 * @param filename
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@MessageMapping("/get")
	@SendToUser
	public ApiResImpl documentByFilename(Message<?> message) throws Exception {
		
		System.out.println("*** ENTERED METHOD documentByFilename ***");
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> headerMap = new HashMap<String,String>();		
		try {
			headerMap = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		String filename = headerMap.get("filename");
		
		Map<String,String> documentMap = new HashMap<String,String>();
		
		byte[] encoded = Files.readAllBytes(Paths.get(directory+"/"+filename));
		String documentText = new String(encoded, Charset.forName("UTF-8"));
		documentMap.put("text", documentText);
		
		System.out.println("We got the following filename " + filename + " with text: " + documentText);
		
		
		return new ApiResImpl("success", documentMap, new RequestId(requestId));
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@MessageMapping("/update_annotator")
	@SendToUser
	public ApiResImpl updateRole(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> map = new HashMap<String,String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		DocumentImpl doc = docRepo.getDocumentByFilename(map.get("filename"));
		System.out.println(map);
		if(map.get("status").equals("Assigned")) {
			doc.setAnnotator(map.get("uin"));
		}
		else {
			doc.setAnnotator("");
		}
		doc.setStatus(map.get("status"));
		docRepo.save(doc);
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}
	
}
