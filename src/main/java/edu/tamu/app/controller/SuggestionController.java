package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.Suggestion;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.suggestor.Suggestor;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/suggest")
public class SuggestionController {

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    // TODO: handle exception gracefully
    @ApiMapping("/{projectName}/{documentName}")
    @Auth(role = "ROLE_USER")
    public ApiResponse getSuggestions(@ApiVariable String projectName, @ApiVariable String documentName) throws IOException {

        Document document = documentRepo.findByProjectNameAndName(projectName, documentName);

        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        for (ProjectSuggestor suggestor : document.getProject().getSuggestors()) {
            suggestions.addAll(((Suggestor) projectServiceRegistry.getService(suggestor.getName())).suggest(document));
        }

        return new ApiResponse(SUCCESS, suggestions);
    }
}
