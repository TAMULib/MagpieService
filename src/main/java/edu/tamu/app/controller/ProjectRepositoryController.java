package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.ProjectRepositoryRepo;
import edu.tamu.app.service.ProjectFactory;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

@RestController
@RequestMapping("/project-repository")
public class ProjectRepositoryController {

    @Autowired
    private ProjectRepositoryRepo projectRepositoryRepo;

    @Autowired
    private ProjectFactory projectFactory;

    /**
     * Endpoint to return list of project repositories.
     * 
     * @return ApiResponse
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getProjectRepositories() {
        return new ApiResponse(SUCCESS, projectRepositoryRepo.findAll());
    }

    @RequestMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getProjectRepository(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, projectRepositoryRepo.findOne(id));
    }

    @RequestMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse create(@WeaverValidatedModel ProjectRepository projectRepository) {
        return new ApiResponse(SUCCESS, projectRepositoryRepo.create(projectRepository));
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse update(@WeaverValidatedModel ProjectRepository projectRepository) {
        return new ApiResponse(SUCCESS, projectRepositoryRepo.update(projectRepository));
    }

    @RequestMapping("/remove")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse remove(@WeaverValidatedModel ProjectRepository projectRepository) {
        projectRepositoryRepo.delete(projectRepository);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/types")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getTypes() {
        return new ApiResponse(SUCCESS, projectFactory.getProjectRepositoryTypes());
    }

}
