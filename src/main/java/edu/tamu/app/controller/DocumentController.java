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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.repo.DocumentRepo;
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
@MessageMapping("/document")
public class DocumentController {
	
	@Autowired
	private DocumentRepo docRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 
	
	@Autowired 
	private VoyagerService voyagerService; 
	
	/**
	 * Endpoint to return marc record.
	 * 
	 * @param 		bibId			@DestinationVariable String bibId
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/marc/{bibId}")
	@Auth
	@SendToUser
	public ApiResponse getMARC(@DestinationVariable String bibId, Message<?> message, @ReqId String requestId) throws Exception {
		return new ApiResponse("success", new FlatMARC(voyagerService.getMARC(bibId)), new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return all documents.
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
	public ApiResponse allDocuments(Message<?> message, @ReqId String requestId) throws Exception {
		Map<String,List<DocumentImpl>> map = new HashMap<String,List<DocumentImpl>>();
		map.put("list", docRepo.findAll());
		return new ApiResponse("success", map, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return document by filename.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/get")
	@Auth
	@SendToUser
	public ApiResponse documentByName(Message<?> message, @ReqId String requestId) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String data = accessor.getNativeHeader("data").get(0).toString();		
		Map<String,String> headerMap = new HashMap<String,String>();
		try {
			headerMap = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		DocumentImpl doc = docRepo.findByName(headerMap.get("name"));
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("annotator", doc.getAnnotator());
		map.put("metadataLabels", doc.getMetadataLabels());
		map.put("notes", doc.getNotes());
				
		return new ApiResponse("success", map, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to return a page of documents.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/page")
	@Auth
	@SendToUser
	public ApiResponse pageDocuments(Message<?> message, @ReqId String requestId) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
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
					if(status[1].length() > 0) {
						documents = docRepo.findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, name, status[0], annotator, name, status[1], annotator);	
					}
					else {
						documents = docRepo.findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, name, status[0], annotator);
					}
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
				if(status[1].length() > 0) {
					documents = docRepo.findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, status[0], annotator, status[1], annotator);
				}
				else {
					documents = docRepo.findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(request, status[0], annotator);
				}
			}
			else {
				if(status[1].length() > 0) {
					documents = docRepo.findByStatusContainingIgnoreCaseOrStatusContainingIgnoreCase(request, status[0], status[1]);
				}
				else {
					documents = docRepo.findByStatusContainingIgnoreCase(request, status[0]);
				}
			}			
		}
		else if(annotator.length() > 0) {
			documents = docRepo.findByAnnotatorContainingIgnoreCase(request, annotator);
		}
		else {
			documents = docRepo.findAll(request);
		}	    
	    return new ApiResponse("success", documents, new RequestId(requestId));
	}

	/**
	 * Endpoint to update document status or annotator.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/update")
	@Auth
	@SendToUser
	public ApiResponse update(Message<?> message, @ReqId String requestId) throws Exception {		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
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
			doc.setAnnotator(map.get("user"));
		}
		doc.setNotes(map.get("notes"));
		doc.setStatus(map.get("status"));
		docRepo.save(doc);
		Map<String, Object> docMap = new HashMap<String, Object>();
		docMap.put("document", doc);
		docMap.put("isNew", "false");
		this.simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse("success", docMap, new RequestId(requestId)));
		
		return new ApiResponse("success", "ok", new RequestId(requestId));
	}
	
	
	/**
	 * Returns the url of the requested txt document.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/txt")
	@Auth
	@SendToUser
	public ApiResponse txt(Message<?> message, @ReqId String requestId) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);		
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
		
		return new ApiResponse("success", txtMap, new RequestId(requestId));
	}
	
	/**
	 * Returns the url of the requested pdf document.
	 * 
	 * @param 		message			Message<?> message
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/pdf")
	@Auth
	@SendToUser
	public ApiResponse pdf(Message<?> message, @ReqId String requestId) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
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
		
		return new ApiResponse("success", pdfMap, new RequestId(requestId));
	}
	
}
