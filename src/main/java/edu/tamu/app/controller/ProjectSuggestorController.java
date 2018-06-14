package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.Suggestion;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectSuggestorRepo;
import edu.tamu.app.service.ProjectFactory;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.suggestor.Suggestor;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

@RestController
@RequestMapping("/project-suggestor")
public class ProjectSuggestorController {

    private static final Logger logger = Logger.getLogger(ProjectSuggestorController.class);

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;
    
    @Autowired
    private ProjectSuggestorRepo projectSuggestorRepo;
    
    @Autowired
    private ProjectFactory projectFactory;
    
    /**
     * Endpoint to return list of project suggestions.
     * 
     * @return ApiResponse
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getProjectRepositories() {
        return new ApiResponse(SUCCESS, projectSuggestorRepo.findAll());
    }

    @RequestMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getProjectRepository(@PathVariable Long id) {
        return new ApiResponse(SUCCESS, projectSuggestorRepo.findOne(id));
    }

    @RequestMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse create(@WeaverValidatedModel ProjectSuggestor projectSuggestor) {
        return new ApiResponse(SUCCESS, projectSuggestorRepo.create(projectSuggestor));
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse update(@WeaverValidatedModel ProjectSuggestor projectSuggestor) {
        return new ApiResponse(SUCCESS, projectSuggestorRepo.update(projectSuggestor));
    }

    @RequestMapping("/remove")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse remove(@WeaverValidatedModel ProjectSuggestor projectSuggestor) {
        projectSuggestorRepo.delete(projectSuggestor);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/types")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getTypes() {
        return new ApiResponse(SUCCESS,projectFactory.getProjectSuggestorTypes());
    }    

    // TODO: handle exception gracefully
    @RequestMapping("/{projectName}/{documentName}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getSuggestions(@PathVariable String projectName, @PathVariable String documentName) throws IOException {

        Document document = documentRepo.findByProjectNameAndName(projectName, documentName);

        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        for (ProjectSuggestor suggestor : document.getProject().getSuggestors()) {

            Optional<Suggestor> potentialSuggestor = Optional.ofNullable((Suggestor) projectServiceRegistry.getService(suggestor.getName()));

            if (potentialSuggestor.isPresent()) {
                suggestions.addAll(potentialSuggestor.get().suggest(document));
            } else {
                logger.warn("Unable to find suggestor from service registry!");
            }

        }

        return new ApiResponse(SUCCESS, suggestions);
    }
}
