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
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.suggestor.Suggestor;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/suggest")
public class SuggestionController {

    private static final Logger logger = Logger.getLogger(SuggestionController.class);

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

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
