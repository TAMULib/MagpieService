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
     * @param       message         Message<?>
     * 
     * @return      ApiResponse
     * 
     * @throws      Exception
     * 
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_USER")
    public ApiResponse getProjects() throws Exception {
        System.out.println(projectRepo.findAll().size());
        return new ApiResponse(SUCCESS, projectRepo.findAll());
    }

}
