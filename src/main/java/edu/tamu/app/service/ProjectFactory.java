package edu.tamu.app.service;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectAuthorityRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ProjectRepositoryRepo;
import edu.tamu.app.model.repo.ProjectSuggestorRepo;
import edu.tamu.app.observer.FileObserverRegistry;
import edu.tamu.app.observer.HeadlessDocumentListener;
import edu.tamu.app.observer.StandardDocumentListener;
import edu.tamu.app.service.authority.Authority;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;
import edu.tamu.app.service.suggestor.Suggestor;
import edu.tamu.app.utilities.FileSystemUtility;

@Service
public class ProjectFactory {

    private static final Logger logger = Logger.getLogger(ProjectFactory.class);

    private static final String DEFAULT_PROJECT_KEY = "default";
    private static final String METADATA_KEY = "metadata";
    private static final String REPOSITORIES_KEY = "repositories";
    private static final String AUTHORITIES_KEY = "authorities";
    private static final String SUGGESTORS_KEY = "suggestors";

    private static final String GLOSS_KEY = "gloss";
    private static final String REPEATABLE_KEY = "repeatable";
    private static final String READ_ONLY_KEY = "readOnly";
    private static final String HIDDEN_KEY = "hidden";
    private static final String REQUIRED_KEY = "required";
    private static final String INPUT_TYPE_KEY = "inputType";
    private static final String DEFAULT_KEY = "default";
    private static final String LABEL_KEY = "label";
    private static final String HEADLESS_KEY = "isHeadless";
    private static final String INGEST_TYPE_KEY = "ingestType";

    @Value("${app.projects.file}")
    private String initialProjectsFile;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private FileObserverRegistry fileObserverRegistry;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectRepositoryRepo projectRepositoryRepo;

    @Autowired
    private ProjectSuggestorRepo projectSuggestorRepo;

    @Autowired
    private ProjectAuthorityRepo projectAuthorityRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    // TODO: initialize projects.json into database and remove this in memory cache
    private JsonNode projectsNode = null;

    public JsonNode readProjectsNode() {
        String json = null;
        try {
            json = new String(Files.readAllBytes(FileSystemUtility.getWindowsSafePath(resourceLoader.getResource("classpath:config").getURL().getPath() + File.separator + initialProjectsFile)));
        } catch (IOException e) {
            logger.error("Error reading metadata json file", e);
        }
        if (json != null) {
            try {
                projectsNode = objectMapper.readTree(json);
            } catch (Exception e) {
                logger.error("Error reading the metadata json with the Object Mapper", e);
            }
        }
        return projectsNode;
    }

    public Project getOrCreateProject(File projectDirectory) {
        return getOrCreateProject(projectDirectory.getName());
    }

    public Project getOrCreateProject(String projectName) {
        Project project = projectRepo.findByName(projectName);
        if (project == null) {
            project = createProject(projectName);
        } else {
            registerListeners(project);
        }
        return project;
    }

    public Project createProject(String projectName) {

        JsonNode projectNode = getProjectNode(projectName);

        // TODO: improve the object mapping for repositories, authorities, and suggestors
        List<ProjectRepository> repositories = getProjectRepositories(projectNode);
        List<ProjectAuthority> authorities = getProjectAuthorities(projectNode);
        List<ProjectSuggestor> suggestors = getProjectSuggestors(projectNode);

        repositories.forEach(service -> {
            projectRepositoryRepo.create(service);
        });

        authorities.forEach(service -> {
            projectAuthorityRepo.create(service);
        });

        suggestors.forEach(service -> {
            projectSuggestorRepo.create(service);
        });

        boolean headless = false;
        if (projectNode.has(HEADLESS_KEY)) {
            headless = projectNode.get(HEADLESS_KEY).asBoolean();
        }

        IngestType ingestType = IngestType.STANDARD;
        if (projectNode.has(INGEST_TYPE_KEY)) {
            ingestType = IngestType.valueOf(projectNode.get(INGEST_TYPE_KEY).asText());
        }

        Project project = projectRepo.create(projectName, ingestType, headless, repositories, authorities, suggestors);

        registerListeners(project);

        return project;
    }

    protected List<ProjectRepository> getProjectRepositories(JsonNode projectNode) {
        List<ProjectRepository> repositories = new ArrayList<ProjectRepository>();
        if (projectNode.has(REPOSITORIES_KEY)) {
            try {
                repositories = objectMapper.readValue(projectNode.get(REPOSITORIES_KEY).toString(), new TypeReference<List<ProjectRepository>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return repositories;
    }

    protected List<ProjectAuthority> getProjectAuthorities(JsonNode projectNode) {
        List<ProjectAuthority> authorities = new ArrayList<ProjectAuthority>();

        if (projectNode.has(AUTHORITIES_KEY)) {
            try {
                authorities = objectMapper.readValue(projectNode.get(AUTHORITIES_KEY).toString(), new TypeReference<List<ProjectAuthority>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return authorities;
    }

    protected List<ProjectSuggestor> getProjectSuggestors(JsonNode projectNode) {
        List<ProjectSuggestor> suggestors = new ArrayList<ProjectSuggestor>();
        if (projectNode.has(SUGGESTORS_KEY)) {
            try {
                suggestors = objectMapper.readValue(projectNode.get(SUGGESTORS_KEY).toString(), new TypeReference<List<ProjectSuggestor>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return suggestors;
    }

    private void registerListeners(Project project) {

        project.getRepositories().forEach(repository -> {
            Repository registeredRepository = (Repository) projectServiceRegistry.getService(repository.getName());
            if (registeredRepository == null) {
                projectServiceRegistry.register(project, repository);
            }
        });

        project.getAuthorities().forEach(authority -> {
            Authority registeredAuthority = (Authority) projectServiceRegistry.getService(authority.getName());
            if (registeredAuthority == null) {
                projectServiceRegistry.register(project, authority);
            }
        });

        project.getSuggestors().forEach(suggestor -> {
            Suggestor registeredSuggestor = (Suggestor) projectServiceRegistry.getService(suggestor.getName());
            if (registeredSuggestor == null) {
                projectServiceRegistry.register(project, suggestor);
            }
        });

    }

    /* TODO generalize against ProjectService commonalities
    public Map<String,List<String>> getProjectServiceTypes(ProjectService projectService) {
        Map<String,List<String>> scaffolds = new HashMap<String,List<String>>();
        getProjectsNode().forEach(projectNode -> {
            List<? extends ProjectService> projectRepositories = getProjectRepositories(projectNode);
            projectRepositories.forEach(projectRepository -> {
                if (!scaffolds.containsKey(projectRepository.getType())) {
                    List<String> settingKeys = new ArrayList<String>();
                    projectRepository.getSettings().forEach(projectSetting -> {
                        settingKeys.add(projectSetting.getKey());
                    });
                    scaffolds.put(projectRepository.getType().toString(), settingKeys);
                }
            });
        });
        return scaffolds;
    }
    */

    public Map<String, List<String>> getProjectRepositoryTypes() {
        Map<String, List<String>> scaffolds = new HashMap<String, List<String>>();
        getProjectsNode().forEach(projectNode -> {
            List<ProjectRepository> projectRepositories = getProjectRepositories(projectNode);
            projectRepositories.forEach(projectRepository -> {
                if (!scaffolds.containsKey(projectRepository.getType())) {
                    List<String> settingKeys = new ArrayList<String>();
                    projectRepository.getSettings().forEach(projectSetting -> {
                        settingKeys.add(projectSetting.getKey());
                    });
                    scaffolds.put(projectRepository.getType().toString(), settingKeys);
                }
            });
        });
        return scaffolds;
    }
    
    public Map<String,List<String>> getProjectSuggestorTypes() {
        Map<String,List<String>> scaffolds = new HashMap<String,List<String>>();
        getProjectsNode().forEach(projectNode -> {
            List<ProjectSuggestor> projectSuggestors = getProjectSuggestors(projectNode);
            projectSuggestors.forEach(projectSuggestor -> {
                if (!scaffolds.containsKey(projectSuggestor.getType())) {
                    List<String> settingKeys = new ArrayList<String>(); 
                    projectSuggestor.getSettings().forEach(projectSetting -> {
                        settingKeys.add(projectSetting.getKey());
                    });
                    scaffolds.put(projectSuggestor.getType().toString(), settingKeys);
                }
            });
        });
        return scaffolds;
    }
    
    public Map<String,List<String>> getProjectAuthorityTypes() {
        Map<String,List<String>> scaffolds = new HashMap<String,List<String>>();
        getProjectsNode().forEach(projectNode -> {
            List<ProjectAuthority> projectAuthorities = getProjectAuthorities(projectNode);
            projectAuthorities.forEach(projectAuthority -> {
                if (!scaffolds.containsKey(projectAuthority.getType())) {
                    List<String> settingKeys = new ArrayList<String>();
                    projectAuthority.getSettings().forEach(projectSetting -> {
                        settingKeys.add(projectSetting.getKey());
                    });
                    scaffolds.put(projectAuthority.getType().toString(), settingKeys);
                }
            });
        });
        return scaffolds;
    }
    
    protected JsonNode getProjectsNode() {
        if (projectsNode == null) {
            projectsNode = readProjectsNode();
        }
        return projectsNode;
    }

    public JsonNode getProjectNode(String projectName) {
        JsonNode profileNode = null;
        getProjectsNode();
        profileNode = projectsNode.get(projectName);
        if (profileNode == null) {
            profileNode = projectsNode.get(DEFAULT_PROJECT_KEY);
        }
        return profileNode;
    }

    public List<MetadataFieldGroup> getProjectFields(String projectName) {

        List<MetadataFieldGroup> projectFields = new ArrayList<MetadataFieldGroup>();

        Project project = projectRepo.findByName(projectName);

        boolean newProject = project == null;

        if (newProject) {
            project = createProject(projectName);
        }

        final Iterable<JsonNode> nodesOfProject = () -> getProjectNode(projectName).get(METADATA_KEY).elements();

        for (JsonNode metadata : nodesOfProject) {
            String gloss = metadata.get(GLOSS_KEY) != null ? metadata.get(GLOSS_KEY).asText() : "";
            Boolean isRepeatable = metadata.get(REPEATABLE_KEY) != null ? metadata.get(REPEATABLE_KEY).asBoolean() : false;
            Boolean isReadOnly = metadata.get(READ_ONLY_KEY) != null ? metadata.get(READ_ONLY_KEY).asBoolean() : false;
            Boolean isHidden = metadata.get(HIDDEN_KEY) != null ? metadata.get(HIDDEN_KEY).asBoolean() : false;
            Boolean isRequired = metadata.get(REQUIRED_KEY) != null ? metadata.get(REQUIRED_KEY).asBoolean() : false;
            InputType inputType = InputType.valueOf(metadata.get(INPUT_TYPE_KEY) != null ? metadata.get(INPUT_TYPE_KEY).asText() : "TEXT");
            String defaultValue = metadata.get(DEFAULT_KEY) != null ? metadata.get(DEFAULT_KEY).asText() : "";

            FieldProfile fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, gloss);
            if (fieldProfile == null) {
                fieldProfile = fieldProfileRepo.create(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue);
            }

            String labelName = metadata.get(LABEL_KEY).asText();

            MetadataFieldLabel metadataFieldLabel = metadataFieldLabelRepo.findByNameAndProfile(labelName, fieldProfile);
            if (metadataFieldLabel == null) {
                metadataFieldLabel = metadataFieldLabelRepo.create(labelName, fieldProfile);
            }

            projectFields.add(new MetadataFieldGroup(metadataFieldLabel));

            if (newProject) {
                project.addProfile(fieldProfile);
            }

        }

        if (newProject) {
            projectRepo.update(project);
        }

        return projectFields;
    }

    public void startProjectFileListeners() {
        String projectsPath = ASSETS_PATH + File.separator + "projects";
        projectRepo.findAll().forEach(project -> {
            if (project.isHeadless()) {
                logger.info("Registering headless document listener: " + projectsPath + File.separator + project.getName());
                fileObserverRegistry.register(new HeadlessDocumentListener(projectsPath, project.getName()));
            } else {
                logger.info("Registering standard document listener: " + projectsPath + File.separator + project.getName());
                fileObserverRegistry.register(new StandardDocumentListener(projectsPath, project.getName()));
            }
        });
    }

}
