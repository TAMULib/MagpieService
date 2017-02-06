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

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;
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
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    private static final Logger logger = Logger.getLogger(DocumentController.class);

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
        return new ApiResponse(SUCCESS, documentRepo.findByProjectNameAndName(projectName, documentName));
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
    @Transactional // without this a save with a field value removed results in it not being removed
    public ApiResponse save(@ApiModel Document document) {
        document = documentRepo.save(document);
        simpMessagingTemplate.convertAndSend("/channel/update-document", new ApiResponse(SUCCESS, document));
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
        
        for (ProjectRepository repository : document.getProject().getRepositories()) {
            try {
                ((Repository) projectServiceRegistry.getService(repository.getName())).push(document);
            } catch (IOException e) {
                logger.error("Exception thrown attempting to push to " + repository.getName() + "!", e);
                e.printStackTrace();
            }
        }

        simpMessagingTemplate.convertAndSend("/channel/update-document", new ApiResponse(SUCCESS, document));

        return new ApiResponse(SUCCESS, "Your item has been successfully published", document);
    }

}
