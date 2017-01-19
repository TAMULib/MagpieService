package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

@RestController
@ApiMapping("/project")
public class ProjectController {
	
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ProjectRepo projectRepo;
    
    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;
    
    private static final Logger logger = Logger.getLogger(DocumentController.class);

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
    
    @ApiMapping("/batchpublish/project/{projectId}/repository/{repositoryId}")
    @Auth(role="ROLE_USER")
    public ApiResponse publishBatch(@ApiVariable Long projectId, @ApiVariable Long repositoryId) {
    	Project project = projectRepo.findOne(projectId);
    	ProjectRepository publishRepository = project.getRepositoryById(repositoryId);
    	if (publishRepository != null) {
    		Repository repositoryService = (Repository) projectServiceRegistry.getService(publishRepository.getName());
    		List<Document> publishableDocuments = project.getPublishableDocuments();
    		for (Document document: publishableDocuments) {
    			try {
    				repositoryService.push(document);
    		        simpMessagingTemplate.convertAndSend("/channel/document", new ApiResponse(SUCCESS, document));
    			} catch (IOException e) {
                    logger.error("Exception thrown attempting to batch push "+document.getName()+" to " + publishRepository.getName() + "!", e);
                    e.printStackTrace();
  				
    			}
    		}
            return new ApiResponse(SUCCESS, "Your batch of "+publishableDocuments.size()+" items(s) was successfully published");
    	}
    	return new ApiResponse(ERROR);
    }

}
