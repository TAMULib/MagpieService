package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectRepo projectRepo;

    /**
     * Endpoint to return list of projects.
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_USER")
    public ApiResponse getProjects() {
        return new ApiResponse(SUCCESS, projectRepo.findAll());
    }

}