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

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

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
@MessageMapping("/document")
public class DocumentController {

	@Value("${app.directory}") 
	private String directory;
	
	@Autowired
	private DocumentRepo docRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 
	
	/**
	 * Endpoint to return all documents.
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
	public ApiResImpl allDocuments(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		Map<String,List<DocumentImpl>> map = new HashMap<String,List<DocumentImpl>>();
		map.put("list", docRepo.findAll());
		return new ApiResImpl("success", map, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return page documents.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/page")
	@SendToUser
	public ApiResImpl pageDocuments(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> headerMap = new HashMap<String,String>();
		try {
			headerMap = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
	    Direction sortDirection;	    
	    if(headerMap.get("direction").equals("asc")) {
	    	sortDirection = Sort.Direction.ASC;
	    }
	    else {
	    	sortDirection = Sort.Direction.DESC;
	    }	    
		String filename = headerMap.get("filename");
		String status = headerMap.get("status");
		String annotator = headerMap.get("annotator");		
		Pageable request = new PageRequest(Integer.parseInt(headerMap.get("page")) - 1, Integer.parseInt(headerMap.get("size")), sortDirection, headerMap.get("field"));
		Page<DocumentImpl> documents = null;				
		if(filename.length() > 0) {			
			if(status.length() > 0) {				
				if(annotator.length() > 0) {
					documents = docRepo.findByFilenameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, filename, status, annotator);	
				}
				else {
					documents = docRepo.findByFilenameContainingIgnoreCaseAndStatusContainingIgnoreCase(request, filename, status);	
				}				
			}
			else if(annotator.length() > 0) {
				documents = docRepo.findByFilenameContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, filename, annotator);				
			}
			else {
				documents = docRepo.findByFilenameContainingIgnoreCase(request, filename);				
			}			
		}
		else if(status.length() > 0) {			
			if(annotator.length() > 0) {
				documents = docRepo.findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, status, annotator);
			}
			else {
				documents = docRepo.findByStatusContainingIgnoreCase(request, status);
			}			
		}
		else if(annotator.length() > 0) {
			documents = docRepo.findByAnnotatorContainingIgnoreCase(request, status);			
		}
		else {
			documents = docRepo.findAll(request);
		}	    
	    return new ApiResImpl("success", documents, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return document by filename.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 */
	@MessageMapping("/get")
	@SendToUser
	public ApiResImpl documentByFilename(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> map = new HashMap<String,String>();
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		String filename = map.get("filename");	
		map.clear();
		byte[] encoded = null;
		try{
			encoded = Files.readAllBytes(Paths.get(directory+"/"+filename));
		}
		catch(Exception e) {
			map.put("text", "File does not exist!");
			return new ApiResImpl("success", map, new RequestId(requestId));
		}
		map.put("text", new String(encoded, Charset.forName("UTF-8")));
		return new ApiResImpl("success", map, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to update document status or annotator.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/update")
	@SendToUser
	public ApiResImpl update(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> map = new HashMap<String,String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		DocumentImpl doc = docRepo.findByFilename(map.get("filename"));
		System.out.println(map);
		if(map.get("status").equals("Open")) {
			doc.setAnnotator("");
		}
		else {
			doc.setAnnotator(map.get("uin"));			
		}
		doc.setStatus(map.get("status"));
		docRepo.save(doc);
		
		Map<String, Object> docMap = new HashMap<String, Object>();
		docMap.put("document", doc);
		docMap.put("isNew", "false");
		this.simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResImpl("success", docMap, new RequestId(requestId)));
		
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}
	
}
