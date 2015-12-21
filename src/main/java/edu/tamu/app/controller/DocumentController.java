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

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.PartialDocument;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.response.marc.FlatMARC;
import edu.tamu.app.service.VoyagerService;
import edu.tamu.app.service.DocumentPushService;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

/** 
 * Document Controller
 * 
 * @author
 *
 */
@Controller
@ApiMapping("/document")
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
	private DocumentPushService documentPushService;
	
	private static final Logger logger = Logger.getLogger(DocumentController.class);
	
	/**
	 * Endpoint to return marc record.
	 * 
	 * @param 		bibId			@DestinationVariable String bibId
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@ApiMapping("/marc/{bibId}")
	@Auth
	public ApiResponse getMARC(@ApiVariable String bibId, Message<?> message) throws Exception {
		return new ApiResponse(SUCCESS, new FlatMARC(voyagerService.getMARC(bibId)));
	}
	
	/**
	 * Endpoint to return all documents.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/all")
	@Auth
	@SendToUser
	public ApiResponse allDocuments(Message<?> message) throws Exception {
		Map<String, List<Document>> map = new HashMap<String, List<Document>>();
		map.put("list", documentRepo.findAll());
		return new ApiResponse(SUCCESS, map);
	}
	
	/**
	 * Endpoint to return document by filename.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@ApiMapping("/get")
	@Auth
	public ApiResponse documentByName(Message<?> message, @Data String data) throws Exception {
		Map<String, String> headerMap = new HashMap<String, String>();
		try {
			headerMap = objectMapper.readValue(data, new TypeReference<HashMap<String, String>>(){});
		} catch (Exception e) {
			logger.error("Error reading data value",e);
			return new ApiResponse(ERROR, "Error reading data value");
		}
		
		Document document = documentRepo.findByName(headerMap.get("name"));
		
		document.setFields(new TreeSet<MetadataFieldGroup>(document.getFields()));
		
		return new ApiResponse(SUCCESS, document);
	}
	
	/**
	 * Endpoint to return a page of documents.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@ApiMapping("/page")
	@Auth
	public ApiResponse pageDocuments(Message<?> message, @Data String data) throws Exception {
		Map<String,String> headerMap = new HashMap<String,String>();
		
		try {
			headerMap = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			logger.error("Error reading data value",e);
			 return new ApiResponse(ERROR, "Error reading data value");
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
		
		Page<PartialDocument> partialDocuments = null;
		
		if(name.length() > 0) {			
			if(status[0].length() > 0) {
				if(annotator.length() > 0) {
					if(status[1].length() > 0) {
						partialDocuments = documentRepo.findByMultipleNameAndStatusAndAnnotatorAsPartialDocument(request, name, status[0], annotator, name, status[1], annotator);	
					}
					else {
						partialDocuments = documentRepo.findByNameAndStatusAndAnnotatorAsPartialDocument(request, name, status[0], annotator);
					}
				}
				else {
					partialDocuments = documentRepo.findByMultipleNameAndStatusAsPartialDocument(request, name, status[0], name, status[1]);	
				}				
			}
			else if(annotator.length() > 0) {
				partialDocuments = documentRepo.findByNameAndAnnotatorAsPartialDocument(request, name, annotator);				
			}
			else {
				partialDocuments = documentRepo.findByNameAsPartialDocument(request, name);
			}			
		}
		else if(status[0].length() > 0) {
			if(annotator.length() > 0) {
				if(status[1].length() > 0) {
					partialDocuments = documentRepo.findByMultipleStatusAndAnnotatorAsPartialDocument(request, status[0], annotator, status[1], annotator);
				}
				else {
					partialDocuments = documentRepo.findByStatusAndAnnotatorAsPartialDocument(request, status[0], annotator);
				}
			}
			else {
				if(status[1].length() > 0) {
					partialDocuments = documentRepo.findByMultipleStatusAsPartialDocument(request, status[0], status[1]);
				}
				else {
					partialDocuments = documentRepo.findByStatusAsPartialDocument(request, status[0]);
				}
			}			
		}
		else if(annotator.length() > 0) {
			partialDocuments = documentRepo.findByAnnotatorAsPartialDocument(request, annotator);
		}
		else {
			partialDocuments = documentRepo.findAllAsPartialDocument(request);
		}
				
	    return new ApiResponse(SUCCESS, partialDocuments);
	}

	/**
	 * Endpoint to update document status or annotator.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@ApiMapping("/update")
	@Auth
	public ApiResponse update(Message<?> message, @Data String data) throws Exception {
		Map<String, String> map = new HashMap<String, String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String, String>>(){});
		} catch (Exception e) {
			logger.error("Error reading data value",e);
			return new ApiResponse(ERROR, "Error reading data value");
		}
		
		int results = documentRepo.quickSave(map.get("name"), (map.get("status").equals("Open")) ? "" : map.get("user"), map.get("status"), map.get("notes"));
		
		if(results < 1) {
			return new ApiResponse(ERROR, "Document not updated");
		}
		
		Map<String, Object> documentMap = new HashMap<String, Object>();
		
		documentMap.put("document", documentRepo.findByName(map.get("name")));
		documentMap.put("isNew", "false");
		
		simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse(SUCCESS, documentMap));
		
		return new ApiResponse(SUCCESS, "ok");
	}
	
	/**
	 * Endpoint to save document.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@ApiMapping("/save")
	@Auth
	public ApiResponse save(Message<?> message, @Data String data) throws Exception {
		
		Document document = null;
		try {
			document = objectMapper.readValue(data, Document.class);
		} catch (Exception e) {
			logger.error("Error reading data value",e);
			return new ApiResponse(ERROR, "Error reading data value");
		}
		
		Map<String, Object> documentMap = new HashMap<String, Object>();
		
		documentMap.put("document", documentRepo.update(document));
		documentMap.put("isNew", "false");
		
		simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse(SUCCESS, documentMap));
		
		return new ApiResponse(SUCCESS, "ok");
	}
	
	/**
	 * Endpoint to save document.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@ApiMapping("/push")
	@Auth
	public ApiResponse push(Message<?> message, @Data String data) throws Exception {
		
		Document document = null;
		try {
			String name = objectMapper.readValue(data, String.class);
			document = documentRepo.findByName(name);
		} catch (Exception e) {
			logger.error("Error reading data value",e);
			return new ApiResponse(ERROR, "Error reading data value");
		}
		
		Map<String, Object> documentMap = new HashMap<String, Object>();
		
		ApiResponse res = documentPushService.push(document);
		documentMap.put("document", res);
		documentMap.put("isNew", "false");
		
		simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse(SUCCESS, documentMap));
		
		return res;
	}
	
		
}
