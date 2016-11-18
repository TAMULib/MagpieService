package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.Suggestion;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.suggestor.Suggestor;
import edu.tamu.framework.SpringContext;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/suggest")
public class SuggestionController {

    @Autowired
    private DocumentRepo documentRepo;

    // TODO: handle exception gracefully
    @ApiMapping("/{projectName}/{documentName}")
    @Auth(role = "ROLE_USER")
    public ApiResponse getSuggestions(@ApiVariable String projectName, @ApiVariable String documentName) throws IOException {

        Document document = documentRepo.findByProjectNameAndName(projectName, documentName);

        Project project = document.getProject();

        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        for (String suggestor : project.getSuggestors()) {
            suggestions.addAll(((Suggestor) SpringContext.bean(suggestor)).suggest(document));
        }

        return new ApiResponse(SUCCESS, suggestions);
    }
}
