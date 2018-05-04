package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceRepo resourceRepo;

    @RequestMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse allResources() {
        return new ApiResponse(SUCCESS, resourceRepo.findAll());
    }

    @RequestMapping("/all/{projectName}/{documentName}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse allResourcesByProjectNameAndDocumentName(@PathVariable String projectName, @PathVariable String documentName) {
        return new ApiResponse(SUCCESS, resourceRepo.findAllByDocumentProjectNameAndDocumentName(projectName, documentName));
    }

}
