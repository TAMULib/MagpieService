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

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;
import edu.tamu.weaver.response.ApiResponse;

/**
 * Document Controller
 * 
 * @author
 *
 */
@RestController
@RequestMapping("/document")
public class DocumentController {

    private static final Logger logger = Logger.getLogger(DocumentController.class);

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Endpoint to return all documents.
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('USER')")
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
    @RequestMapping("/get/{projectName}/{documentName}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse documentByName(@PathVariable String projectName, @PathVariable String documentName) {
        return new ApiResponse(SUCCESS, documentRepo.findByProjectNameAndName(projectName, documentName));
    }

    /**
     * Endpoint to return a page of documents.
     * 
     * @param data
     * @ApiData Map<String, Object>
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/page")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse pageDocuments(@RequestBody Map<String, Object> data) {

        Direction sortDirection;

        JsonNode dataNode = objectMapper.convertValue(data, JsonNode.class);

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
        if (dataNode.get("filters").get("projects").size() > 0 && dataNode.get("filters").get("projects").isArray()) {
            for (final JsonNode objNode : dataNode.get("filters").get("projects")) {
                filters.put("projects", arrayNodeToStringArray((ArrayNode) objNode));
            }
        }
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
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public ApiResponse save(@RequestBody Document document) {
        document = documentRepo.fullSave(document);
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
    @RequestMapping("/push/{projectName}/{documentName}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse push(@PathVariable String projectName, @PathVariable String documentName) {
        Document document = documentRepo.findByProjectNameAndName(projectName, documentName);

        for (ProjectRepository repository : document.getProject().getRepositories()) {
            try {
                document = ((Repository) projectServiceRegistry.getService(repository.getName())).push(document);
            } catch (IOException e) {
                logger.error("Exception thrown attempting to push to " + repository.getName() + "!", e);
                e.printStackTrace();
                return new ApiResponse(ERROR, "There was an error publishing this item");
            }
        }

        return new ApiResponse(SUCCESS, "Your item has been successfully published", document);
    }

}
