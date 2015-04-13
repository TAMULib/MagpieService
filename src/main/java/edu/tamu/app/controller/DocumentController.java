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
	public ApiResImpl documentByName(Message<?> message) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> headerMap = new HashMap<String,String>();
		try {
			headerMap = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		DocumentImpl doc = docRepo.findByName(headerMap.get("name"));
		Map<String,String> map = new HashMap<String,String>();
		map.put("annotator", doc.getAnnotator());
		map.put("notes", doc.getNotes());
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
		String name = headerMap.get("name");
		String annotator = headerMap.get("annotator");
		String[] status = new String[2];
		status[0] = headerMap.get("status");
		if(status[0].equals("Assigned")) {
			status[1] = "Rejected";
		}
		else {
			status[1] = "";
		}
		Pageable request = new PageRequest(Integer.parseInt(headerMap.get("page")) - 1, Integer.parseInt(headerMap.get("size")), sortDirection, headerMap.get("field"));
		Page<DocumentImpl> documents = null;				

		if(name.length() > 0) {			
			if(status[0].length() > 0) {
				if(annotator.length() > 0) {
					documents = docRepo.findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, name, status[0], annotator, name, status[1], annotator);	
				}
				else {
					documents = docRepo.findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatusContainingIgnoreCase(request, name, status[0], name, status[1]);	
				}				
			}
			else if(annotator.length() > 0) {
				documents = docRepo.findByNameContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, name, annotator);				
			}
			else {
				documents = docRepo.findByNameContainingIgnoreCase(request, name);
			}			
		}
		else if(status[0].length() > 0) {
			if(annotator.length() > 0) {
				documents = docRepo.findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, status[0], annotator, status[1], annotator);
			}
			else {
				documents = docRepo.findByStatusContainingIgnoreCaseOrStatusContainingIgnoreCase(request, status[0], status[1]);
			}			
		}
		else if(annotator.length() > 0) {
			documents = docRepo.findByAnnotatorContainingIgnoreCase(request, annotator);
		}
		else {
			documents = docRepo.findAll(request);
		}	    
	    return new ApiResImpl("success", documents, new RequestId(requestId));
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
		DocumentImpl doc = docRepo.findByName(map.get("name"));
		if(map.get("status").equals("Open")) {
			doc.setAnnotator("");
		}
		else {
			doc.setAnnotator(map.get("uin"));
		}
		doc.setNotes(map.get("notes"));
		doc.setStatus(map.get("status"));
		docRepo.save(doc);
		Map<String, Object> docMap = new HashMap<String, Object>();
		docMap.put("document", doc);
		docMap.put("isNew", "false");
		this.simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResImpl("success", docMap, new RequestId(requestId)));
		
		return new ApiResImpl("success", "ok", new RequestId(requestId));
	}
	
	
	/**
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@MessageMapping("/txt")
	@SendToUser
	public ApiResImpl txt(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> map = new HashMap<String,String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		DocumentImpl doc = docRepo.findByName(map.get("name"));
		
		Map<String, Object> txtMap = new HashMap<String, Object>();
		txtMap.put("uri", doc.getTxtUri());
		
		return new ApiResImpl("success", txtMap, new RequestId(requestId));
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@MessageMapping("/pdf")
	@SendToUser
	public ApiResImpl pdf(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> map = new HashMap<String,String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		DocumentImpl doc = docRepo.findByName(map.get("name"));
		
		Map<String, Object> pdfMap = new HashMap<String, Object>();
		pdfMap.put("uri", doc.getPdfUri());
		
		return new ApiResImpl("success", pdfMap, new RequestId(requestId));
	}
	
}
