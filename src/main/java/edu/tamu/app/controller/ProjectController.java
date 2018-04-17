package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private static final Logger logger = Logger.getLogger(ProjectController.class);

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    /**
     * Endpoint to return list of projects.
     * 
     * @return ApiResponse
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getProjects() {
        return new ApiResponse(SUCCESS, projectRepo.findAll());
    }

    @RequestMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getProject(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, projectRepo.findOne(id));
    }

    @RequestMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse create(@WeaverValidatedModel Project project) {
        return new ApiResponse(SUCCESS, projectRepo.create(project));
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse update(@WeaverValidatedModel Project project) {
        //TODO There's hopefully a better way to merge the old project state with the new one
        Project currentProject = projectRepo.findOne(project.getId());
        project.setDocuments(currentProject.getDocuments());
        project.setProfiles(currentProject.getProfiles());
        return new ApiResponse(SUCCESS, projectRepo.update(project));
    }

    @RequestMapping("/remove")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse remove(@WeaverValidatedModel Project project) {
        projectRepo.delete(project);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint for batch publishing to a given repository all Accepted documents of a project
     * 
     * @param projectId
     * @param repositoryId
     * 
     * @return ApiResponse
     */
    @RequestMapping("/batchpublish/project/{projectId}/repository/{repositoryId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse publishBatch(@PathVariable Long projectId, @PathVariable Long repositoryId) {
        Project project = projectRepo.read(projectId);
        ProjectRepository publishRepository = project.getRepositoryById(repositoryId);
        if (publishRepository != null) {
            Repository repositoryService = (Repository) projectServiceRegistry.getService(publishRepository.getName());
            List<Document> publishableDocuments = project.getPublishableDocuments();
            boolean errorFlag = false;
            for (Document document : publishableDocuments) {
                try {
                    repositoryService.push(document);
                } catch (IOException e) {
                    logger.error("Exception thrown attempting to batch push " + document.getName() + " to " + publishRepository.getName() + "!", e);
                    e.printStackTrace();
                    errorFlag = true;
                }
            }
            if (errorFlag == false) {
                return new ApiResponse(SUCCESS, "Your batch of " + publishableDocuments.size() + " items(s) was successfully published");
            }
        }
        return new ApiResponse(ERROR, "There was an error with the batch publish");
    }

}
