package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.repo.ProjectAuthorityRepo;
import edu.tamu.app.service.ProjectFactory;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

@RestController
@RequestMapping("/project-authority")
public class ProjectAuthorityController {

    @Autowired
    private ProjectAuthorityRepo projectAuthorityRepo;

    @Autowired
    private ProjectFactory projectFactory;

    /**
     * Endpoint to return list of project authorities.
     *
     * @return ApiResponse
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getProjectAuthorities() {
        return new ApiResponse(SUCCESS, projectAuthorityRepo.findAll());
    }

    @RequestMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getProjectAuthority(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, projectAuthorityRepo.findOne(id));
    }

    @RequestMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse create(@WeaverValidatedModel ProjectAuthority projectAuthority) {
        return new ApiResponse(SUCCESS, projectAuthorityRepo.create(projectAuthority));
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse update(@WeaverValidatedModel ProjectAuthority projectAuthority) {
        return new ApiResponse(SUCCESS, projectAuthorityRepo.update(projectAuthority));
    }

    @RequestMapping("/remove")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse remove(@WeaverValidatedModel ProjectAuthority projectAuthority) {
        projectAuthorityRepo.delete(projectAuthority);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/types")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getTypes() {
        return new ApiResponse(SUCCESS,projectFactory.getProjectAuthorityTypes());
    }

}
