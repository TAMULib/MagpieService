package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static edu.tamu.weaver.response.ApiStatus.ERROR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import antlr.collections.List;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectSetting;
import edu.tamu.app.model.repo.ProjectAuthorityRepo;
import edu.tamu.app.service.ProjectFactory;
import edu.tamu.app.utilities.FileSystemUtility;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

import static edu.tamu.app.Initialization.ASSETS_PATH;

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

    @RequestMapping("/upload-csv")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            String uploadPath = ASSETS_PATH + File.separator + "uploads";
            FileSystemUtility.createDirectory(uploadPath);
            file.transferTo(new File(uploadPath + File.separator + file.getName()));
            return new ApiResponse(SUCCESS,"CSV upload successful", uploadPath + File.separator + file.getOriginalFilename());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ApiResponse(ERROR, "CSV upload failed");
        }
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse update(@WeaverValidatedModel ProjectAuthority projectAuthority) {
        return new ApiResponse(SUCCESS, projectAuthorityRepo.update(projectAuthority));
    }

    @RequestMapping("/delete")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse delete(@WeaverValidatedModel ProjectAuthority projectAuthority) {
        //TODO The WeaverValidatedModel isn't populating the projects associated with the projectAuthority, so we have to get it fresh from the repo here
        projectAuthorityRepo.delete(projectAuthorityRepo.getOne(projectAuthority.getId()));
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/types")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getTypes() {
        return new ApiResponse(SUCCESS,projectFactory.getProjectAuthorityTypes());
    }

}
