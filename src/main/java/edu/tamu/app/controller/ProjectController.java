package edu.tamu.app.controller;

import static edu.tamu.app.Initialization.ASSETS_PATH;
import static edu.tamu.app.Initialization.PROJECTS_PATH;
import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectAuthorityRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ProjectRepositoryRepo;
import edu.tamu.app.model.repo.ProjectSuggestorRepo;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.service.ProjectFactory;
import edu.tamu.app.service.PropertyProtectionService;
import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Destination;
import edu.tamu.app.utilities.FileSystemUtility;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.validation.aspect.annotation.WeaverValidatedModel;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    @Autowired
    private ProjectAuthorityRepo projectAuthorityRepo;

    @Autowired
    private ProjectSuggestorRepo projectSuggestorRepo;

    @Autowired
    private ProjectRepositoryRepo projectRepositoryRepo;

    @Autowired
    private ProjectFactory projectFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SyncService syncService;

    @Autowired
    private PropertyProtectionService propertyProtectionService;

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
        return new ApiResponse(SUCCESS, projectRepo.getById(id));
    }

    @RequestMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse create(@WeaverValidatedModel Project project) throws IOException {
        project = projectRepo.create(project);
        projectFactory.registerServiceListeners(project);
        logger.info("creating directory: "+ASSETS_PATH + File.separator + PROJECTS_PATH + File.separator + project.getName());
        FileSystemUtility.createDirectory(ASSETS_PATH + File.separator + PROJECTS_PATH + File.separator + project.getName());
        return new ApiResponse(SUCCESS, project);
    }

    @RequestMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse update(@WeaverValidatedModel Project project) {

        Project currentProject = projectRepo.getById(project.getId());
        boolean refreshProjectListener = (currentProject.isHeadless() != project.isHeadless());

        //we need to populate the values of any protected ProjectService properties by getting the full entities from the repo
        List<Long> projectRepositoryIds = new ArrayList<Long>();
        project.getRepositories().forEach(pr -> {
            projectRepositoryIds.add(pr.getId());
        });
        project.setRepositories(projectRepositoryRepo.findAllById(projectRepositoryIds));

        project.getRepositories().forEach(r -> {
            r.setPropertyProtectionService(propertyProtectionService);
        });

        List<Long> projectAuthorityIds = new ArrayList<Long>();
        project.getAuthorities().forEach(pa -> {
            projectAuthorityIds.add(pa.getId());
        });
        project.setAuthorities(projectAuthorityRepo.findAllById(projectAuthorityIds));

        project.getAuthorities().forEach(a -> {
            a.setPropertyProtectionService(propertyProtectionService);
        });

        List<Long> projectSuggestorIds = new ArrayList<Long>();
        project.getSuggestors().forEach(ps -> {
            projectSuggestorIds.add(ps.getId());
        });
        project.setSuggestors(projectSuggestorRepo.findAllById(projectSuggestorIds));

        project.getSuggestors().forEach(s -> {
            s.setPropertyProtectionService(propertyProtectionService);
        });

        BeanUtils.copyProperties(project, currentProject, "documents","profiles");
        currentProject = projectRepo.update(currentProject);
        projectFactory.registerServiceListeners(currentProject);
        if (refreshProjectListener) {
            projectFactory.stopProjectFileListener(currentProject);
            projectFactory.startProjectFileListener(currentProject);
        }
        return new ApiResponse(SUCCESS, currentProject);
    }

    @RequestMapping("/remove")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse remove(@WeaverValidatedModel Project project) {
        //The cascade is set up so we don't actually need to remove the Project from the Project Services before deleting the Project,
        //but we DO need to broadcast that the Project Services no longer have an association with that project.
        project.getAuthorities().forEach(authority -> {
            authority.removeProject(project);
            projectAuthorityRepo.update(authority);
        });

        project.getSuggestors().forEach(suggestor -> {
            suggestor.removeProject(project);
            projectSuggestorRepo.update(suggestor);
        });

        project.getRepositories().forEach(repository -> {
            repository.removeProject(project);
            projectRepositoryRepo.update(repository);
        });

        //get the complete project, with documents
        Project removableProject = projectRepo.getById(project.getId());

        if (removableProject.getDocuments().isEmpty() != true) {
            final String projectName = removableProject.getName();
            removableProject.getDocuments().forEach(document -> {
                resourceRepo.deleteAll(resourceRepo.findAllByDocumentProjectNameAndDocumentName(projectName, document.getName()));
            });
        }

        projectRepo.delete(removableProject);
        projectFactory.stopProjectFileListener(removableProject);
        projectServiceRegistry.deregisterAuxiliaryServices(removableProject);
        return new ApiResponse(SUCCESS);
    }

    @RequestMapping("/{projectId}/add-field-profile")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse addFieldProfile(@PathVariable Long projectId, @RequestBody JsonNode data) {
        Project currentProject = projectRepo.getById(projectId);
        FieldProfile existingFieldProfile = fieldProfileRepo.findByProjectAndGloss(currentProject, data.get("fieldProfile").get("gloss").toString());
        if (existingFieldProfile == null) {
            try {
                FieldProfile fieldProfile = objectMapper.readValue(data.get("fieldProfile").toString(), new TypeReference<FieldProfile>() {
                });

                List<Map<String,String>> labels = objectMapper.readValue(data.get("labels").toString(), new TypeReference<List<Map<String,String>>>() {
                });

                fieldProfile.setProject(currentProject);
                fieldProfile = fieldProfileRepo.save(fieldProfile);
                for (Map<String,String> label: labels) {
                    MetadataFieldLabel metadataFieldLabel = metadataFieldLabelRepo.findByNameAndProfile(label.get("name"), fieldProfile);
                    if (metadataFieldLabel == null) {
                        metadataFieldLabel = metadataFieldLabelRepo.create(label.get("name"), fieldProfile);
                    }
                }
                currentProject.addProfile(fieldProfile);
                return new ApiResponse(SUCCESS,"Field Profile added", projectRepo.update(currentProject));

            } catch (IOException e) {
                e.printStackTrace();
                new ApiResponse(ERROR, "There was an error processing the Field Profile.");
            }
        }
        return new ApiResponse(ERROR, "A Field Profile with that name already exists.");
    }

    @RequestMapping("/{projectId}/update-field-profile")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse updateFieldProfile(@PathVariable Long projectId, @RequestBody JsonNode data) {
        Project currentProject = projectRepo.getById(projectId);

        try {
            FieldProfile fieldProfile = objectMapper.readValue(data.get("fieldProfile").toString(), new TypeReference<FieldProfile>() {
            });

            List<MetadataFieldLabel> labels = objectMapper.readValue(data.get("labels").toString(), new TypeReference<List<MetadataFieldLabel>>() {
            });

            fieldProfile.setProject(currentProject);
            fieldProfile = fieldProfileRepo.save(fieldProfile);
            for (MetadataFieldLabel label: labels) {
                if (label.getId() == null) {
                    label = metadataFieldLabelRepo.create(label.getName(), fieldProfile);
                } else {
                    label = metadataFieldLabelRepo.save(label);
                }
            }
            fieldProfileRepo.save(fieldProfile);
            return new ApiResponse(SUCCESS, "Field Profile updated", projectRepo.update(currentProject));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ApiResponse(ERROR, "There was an error updating the Field Profile");
    }

    @RequestMapping("/ingest-types")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getIngestTypes() {
        return new ApiResponse(SUCCESS, new ArrayList<IngestType>(Arrays.asList(IngestType.values())));
    }

    @RequestMapping("/input-types")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getInputTypes() {
        return new ApiResponse(SUCCESS, new ArrayList<InputType>(Arrays.asList(InputType.values())));
    }

    @RequestMapping("/field-profile/{fieldProfileId}/get-labels")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getFieldProfileLabels(@PathVariable Long fieldProfileId) {
        return new ApiResponse(SUCCESS, metadataFieldLabelRepo.findByProfileId(fieldProfileId));
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
            Destination repositoryService = (Destination) projectServiceRegistry.getService(publishRepository.getName());
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

    // TODO: handle exception gracefully
    /**
     * Synchronizes the project directory with the database for a single project.
     *
     * @param Long id
     *   The ID of the specific project to synchronize.
     *
     * @return ApiResponse
     * @throws IOException
     *
     */
    @RequestMapping("/sync/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse syncDocuments(@PathVariable Long id) throws IOException {
        ApiResponse response = null;
        logger.info("Syncronizing projects with database.");
        if (id == null) {
            response = new ApiResponse(ERROR, "No valid project ID specified.");
        }
        else {
            syncService.sync(id);
            response = new ApiResponse(SUCCESS, "Syncronized project ID " + id + " with database.");
        }
        return response;
    }

}
