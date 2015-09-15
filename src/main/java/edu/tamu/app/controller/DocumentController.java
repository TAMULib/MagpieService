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
import java.util.TreeSet;

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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
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
	private ObjectMapper objectMapper;
	
	@Autowired 
	private VoyagerService voyagerService; 
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private MetadataFieldGroupRepo metadataFieldGroupRepo;
	
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
		Map<String, List<Document>> map = new HashMap<String, List<Document>>();
		map.put("list", documentRepo.findAll());
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
	public ApiResponse documentByName(Message<?> message, @ReqId String requestId, @Data String data) throws Exception {
		Map<String, String> headerMap = new HashMap<String, String>();
		try {
			headerMap = objectMapper.readValue(data, new TypeReference<HashMap<String, String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Document document = documentRepo.findByName(headerMap.get("name"));
		
		document.setFields(new TreeSet<MetadataFieldGroup>(document.getFields()));
		
		return new ApiResponse("success", document, new RequestId(requestId));
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
	public ApiResponse pageDocuments(Message<?> message, @ReqId String requestId, @Data String data) throws Exception {
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
		
		Page<Object> objects = null;
		
		if(name.length() > 0) {			
			if(status[0].length() > 0) {
				if(annotator.length() > 0) {
					if(status[1].length() > 0) {
						objects = documentRepo.findByMultipleNameAndStatusAndAnnotatorAsObject(request, name, status[0], annotator, name, status[1], annotator);	
					}
					else {
						objects = documentRepo.findByNameAndStatusAndAnnotatorAsObject(request, name, status[0], annotator);
					}
				}
				else {
					objects = documentRepo.findByMultipleNameAndStatusAsObject(request, name, status[0], name, status[1]);	
				}				
			}
			else if(annotator.length() > 0) {
				objects = documentRepo.findByNameAndAnnotatorAsObject(request, name, annotator);				
			}
			else {
				objects = documentRepo.findByNameAsObject(request, name);
			}			
		}
		else if(status[0].length() > 0) {
			if(annotator.length() > 0) {
				if(status[1].length() > 0) {
					objects = documentRepo.findByMultipleStatusAndAnnotatorAsObject(request, status[0], annotator, status[1], annotator);
				}
				else {
					objects = documentRepo.findByStatusAndAnnotatorAsObject(request, status[0], annotator);
				}
			}
			else {
				if(status[1].length() > 0) {
					objects = documentRepo.findByMultipleStatusAsObject(request, status[0], status[1]);
				}
				else {
					objects = documentRepo.findByStatusAsObject(request, status[0]);
				}
			}			
		}
		else if(annotator.length() > 0) {
			objects = documentRepo.findByAnnotatorAsObject(request, annotator);
		}
		else {
			objects = documentRepo.findAllAsObject(request);
		}
				
	    return new ApiResponse("success", objects, new RequestId(requestId));
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
	public ApiResponse update(Message<?> message, @ReqId String requestId, @Data String data) throws Exception {
		Map<String, String> map = new HashMap<String, String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String, String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int results = documentRepo.quickSave(map.get("name"), (map.get("status").equals("Open")) ? "" : map.get("user"), map.get("status"), map.get("notes"));
		
		if(results < 1) return new ApiResponse("failure", "document no updated", new RequestId(requestId));
		
		Map<String, Object> documentMap = new HashMap<String, Object>();
		
		documentMap.put("document", documentRepo.findByName(map.get("name")));
		documentMap.put("isNew", "false");
		
		this.simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse("success", documentMap, new RequestId(requestId)));
		
		return new ApiResponse("success", "ok", new RequestId(requestId));
	}
	
	/**
	 * Endpoint to save document.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/save")
	@Auth
	@SendToUser
	public ApiResponse save(Message<?> message, @ReqId String requestId, @Data String data) throws Exception {
		
		Document document = null;
		try {
			document = objectMapper.readValue(data, Document.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String, Object> documentMap = new HashMap<String, Object>();
		
		documentMap.put("document", documentRepo.update(document));
		documentMap.put("isNew", "false");
		
		this.simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse("success", documentMap, new RequestId(requestId)));
		
		return new ApiResponse("success", "ok", new RequestId(requestId));
	}
		
}
