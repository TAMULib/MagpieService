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
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.tamu.app.authority.VoyagerAuthority;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.response.marc.FlatMARC;
import edu.tamu.app.service.DocumentPushService;
import edu.tamu.framework.aspect.annotation.ApiData;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Document Controller
 * 
 * @author
 *
 */
@RestController
@ApiMapping("/document")
public class DocumentController {

	@Autowired
	private VoyagerAuthority voyagerAuthority;

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
	 * @param bibId
	 * @ApiVariable String
	 * 
	 * @return ApiResponse
	 * 
	 * @throws Exception
	 * 
	 */
	@ApiMapping("/marc/{bibId}")
	@Auth(role = "ROLE_USER")
	public ApiResponse getMARC(@ApiVariable String bibId) throws Exception {
		return new ApiResponse(SUCCESS, new FlatMARC(voyagerAuthority.fetchMARC(bibId)));
	}

	/**
	 * Endpoint to return all documents.
	 * 
	 * @return ApiResponse
	 * 
	 */
	@ApiMapping("/all")
	@Auth(role = "ROLE_USER")
	public ApiResponse allDocuments() {
		return new ApiResponse(SUCCESS, documentRepo.findAll());
	}

	/**
	 * Endpoint to return document by filename.
	 * 
	 * @param name
	 * @ApiVariable String
	 * 
	 * @return ApiResponse
	 * 
	 */
	@ApiMapping("/get/{projectName}/{documentName}")
	@Auth(role = "ROLE_USER")
	public ApiResponse documentByName(@ApiVariable String projectName, @ApiVariable String documentName) {
		
		System.out.println("\nPROJECT NAME: " + projectName);
		System.out.println("\nDOCUMENT NAME: " + documentName);
		Document document = documentRepo.findByProjectNameAndName(projectName, documentName);
		
		System.out.println("\nGET " + document.getId() + "\n");
		document.getFields().forEach(field -> {
			System.out.println("\n" + field.getLabel().getName());
			field.getValues().forEach(value -> {
				System.out.println("  " + value.getValue());
			});
		});
		
		return new ApiResponse(SUCCESS, document);
	}

	/**
	 * Endpoint to return a page of documents.
	 * 
	 * @param dataNode
	 * @ApiData JsonNode
	 * 
	 * @return ApiResponse
	 * 
	 */
	@ApiMapping("/page")
	@Auth(role = "ROLE_USER")
	public ApiResponse pageDocuments(@ApiData JsonNode dataNode) {

		Direction sortDirection;

		if (dataNode.get("sort").get("direction").asText().equals("asc")) {
			sortDirection = Sort.Direction.ASC;
		} else {
			sortDirection = Sort.Direction.DESC;
		}

		Pageable request = new PageRequest(dataNode.get("page").get("number").asInt() - 1, dataNode.get("page").get("size").asInt(), sortDirection, dataNode.get("sort").get("field").asText());

		Map<String, String[]> filters = new HashMap<String, String[]>();

		filters.put("name", arrayNodeToStringArray((ArrayNode) dataNode.get("filters").get("name")));
		filters.put("annotator", arrayNodeToStringArray((ArrayNode) dataNode.get("filters").get("annotator")));
		filters.put("status", arrayNodeToStringArray((ArrayNode) dataNode.get("filters").get("status")));

		return new ApiResponse(SUCCESS, documentRepo.pageableDynamicDocumentQuery(filters, request));
	}

	private String[] arrayNodeToStringArray(ArrayNode arrayNode) {
		String[] array = new String[arrayNode.size()];
		Iterator<JsonNode> arrayIterator = arrayNode.elements();
		int i = 0;
		while (arrayIterator.hasNext()) {
			array[i++] = arrayIterator.next().asText();
		}
		return array;
	}

	/**
	 * Endpoint to update document status or annotator.
	 * 
	 * @param data
	 * @ApiData Map<String, String>
	 * 
	 * @return ApiResponse
	 * 
	 */
	@ApiMapping("/update")
	@Auth(role = "ROLE_USER")
	public ApiResponse update(@ApiData Map<String, String> data) {

		int results;

		if (data.get("user") != null) {
			results = documentRepo.quickSave(data.get("name"), (data.get("status").equals("Open")) ? "" : data.get("user"), data.get("status"), data.get("notes"));
		} else {
			results = documentRepo.quickUpdateStatus(data.get("name"), data.get("status"));
		}

		if (results < 1) {
			return new ApiResponse(ERROR, "Document not updated");
		}

		simpMessagingTemplate.convertAndSend("/channel/document", new ApiResponse(SUCCESS));

		return new ApiResponse(SUCCESS);
	}

	/**
	 * Endpoint to save document.
	 * 
	 * @param document
	 * @ApiData Document
	 * 
	 * @return ApiResponse
	 * 
	 */
	@ApiMapping("/save")
	@Auth(role = "ROLE_USER")
	public ApiResponse save(@ApiModel Document document) {
		System.out.println("\nBEFORE\n");
		document.getFields().forEach(field -> {
			System.out.println("\n" + field.getLabel().getName());
			field.getValues().forEach(value -> {
				System.out.println("  " + value.getValue());
			});
		});

		document = documentRepo.save(document);
		
		System.out.println("\nAFTER " + document.getId() + "\n");
		document.getFields().forEach(field -> {
			System.out.println("\n" + field.getLabel().getName());
			field.getValues().forEach(value -> {
				System.out.println("  " + value.getValue());
			});
		});

		simpMessagingTemplate.convertAndSend("/channel/document", new ApiResponse(SUCCESS));

		return new ApiResponse(SUCCESS);
	}

	/**
	 * Endpoint to push document to IR.
	 * 
	 * @param name
	 * @ApiVariable String
	 * 
	 * @return ApiResponse
	 * 
	 */
	@ApiMapping("/push/{projectName}/{documentName}")
	@Auth(role = "ROLE_USER")
	public ApiResponse push(@ApiVariable String projectName, @ApiVariable String documentName) {

		Document document = documentRepo.findByProjectNameAndName(projectName, documentName);

		try {
			document = documentPushService.push(document);
		} catch (Exception e) {
			logger.error("The documentPushService threw an exception", e);
			return new ApiResponse(ERROR, e.getMessage());
		}

		simpMessagingTemplate.convertAndSend("/channel/document", new ApiResponse(SUCCESS));

		return new ApiResponse(SUCCESS, "Your item has been successfully published", document);
	}

}
