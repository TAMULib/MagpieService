/* 
 * MetadataFieldController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Metadata Field Controller
 * 
 * @author
 *
 */
@RestController
@ApiMapping("/metadata")
public class MetadataController {

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    /**
     * Endpoint to unlock a given project
     * 
     * @param projectToUnlock
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/unlock/{projectToUnlock}")
    @Auth(role = "ROLE_USER")
    public ApiResponse unlockProject(@ApiVariable String projectToUnlock) {
        Project project = projectRepo.findByName(projectToUnlock);
        project.setIsLocked(false);
        projectRepo.save(project);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to return all by status metadata fields.
     * 
     * @param status
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/status/{status}")
    @Auth(role = "ROLE_USER")
    public ApiResponse published(@ApiVariable String status) {
        List<List<String>> metadata = new ArrayList<List<String>>();
        documentRepo.findByStatus(status).forEach(document -> {
            new TreeSet<MetadataFieldGroup>(document.getFields()).forEach(field -> {
                field.getValues().forEach(value -> {
                    List<String> documentMetadata = new ArrayList<String>();
                    documentMetadata.add(field.getLabel().getName());
                    documentMetadata.add(value.getValue());
                    metadata.add(documentMetadata);
                });
            });
        });
        return new ApiResponse(SUCCESS, metadata);
    }

    /**
     * Endpoint to return all metadata fields.
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_USER")
    public ApiResponse all() {
        Map<String, List<MetadataFieldGroup>> metadataMap = new HashMap<String, List<MetadataFieldGroup>>();
        metadataMap.put("list", metadataFieldGroupRepo.findAll());
        return new ApiResponse(SUCCESS, metadataMap);
    }

}
