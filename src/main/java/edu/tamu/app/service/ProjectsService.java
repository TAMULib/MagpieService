package edu.tamu.app.service;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.authority.Authority;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;
import edu.tamu.app.service.suggestor.Suggestor;
import edu.tamu.app.utilities.FileSystemUtility;
import edu.tamu.framework.model.ApiResponse;

@Service
public class ProjectsService {

    private static final Logger logger = Logger.getLogger(ProjectsService.class);

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

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Value("${app.host}")
    private String host;

    @Value("${app.mount}")
    private String mount;

    private Map<String, Project> projects = new HashMap<String, Project>();

    private Map<String, List<MetadataFieldGroup>> fields = new HashMap<String, List<MetadataFieldGroup>>();

    private JsonNode projectsNode = null;

    public JsonNode readProjectsNode() {
        String json = null;
        try {
            json = new String(Files.readAllBytes(FileSystemUtility.getWindowsSafePath(
                    resourceLoader.getResource("classpath:config").getURL().getPath() + "/projects.json")));
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

    public synchronized Project getOrCreateProject(File projectDirectory) {
        String projectName = getName(projectDirectory);
        return getOrCreateProject(projectName);
    }

    public synchronized Project getOrCreateProject(String projectName) {

        Project project = projects.get(projectName);
        if (project == null) {
            project = projectRepo.findByName(projectName);
        }
        if (project == null) {

            JsonNode projectNode = getProjectNode(projectName);

            // TODO: improve the object mapping for repositories, authorities,
            // and suggestors

            List<ProjectRepository> repositories = new ArrayList<ProjectRepository>();
            try {
                repositories = objectMapper.readValue(projectNode.get(REPOSITORIES_KEY).toString(),
                        new TypeReference<List<ProjectRepository>>() {
                        });

            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<ProjectAuthority> authorities = new ArrayList<ProjectAuthority>();
            try {
                authorities = objectMapper.readValue(projectNode.get(AUTHORITIES_KEY).toString(),
                        new TypeReference<List<ProjectAuthority>>() {
                        });

            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<ProjectSuggestor> suggestors = new ArrayList<ProjectSuggestor>();
            try {
                suggestors = objectMapper.readValue(projectNode.get(SUGGESTORS_KEY).toString(),
                        new TypeReference<List<ProjectSuggestor>>() {
                        });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            project = projectRepo.create(projectName, repositories, authorities, suggestors);

            try {
                simpMessagingTemplate.convertAndSend("/channel/project",
                        new ApiResponse(SUCCESS, projectRepo.findAll()));
            } catch (Exception e) {
                logger.error("Error broadcasting new project", e);
            }
        }

        project.getRepositories().forEach(repository -> {
            Repository registeredRepository = (Repository) projectServiceRegistry.getService(repository.getName());
            if (registeredRepository == null) {
                projectServiceRegistry.register(repository);
            }
        });

        project.getAuthorities().forEach(authority -> {
            Authority registeredAuthority = (Authority) projectServiceRegistry.getService(authority.getName());
            if (registeredAuthority == null) {
                projectServiceRegistry.register(authority);
            }
        });

        project.getSuggestors().forEach(suggestor -> {
            Suggestor registeredSuggestor = (Suggestor) projectServiceRegistry.getService(suggestor.getName());
            if (registeredSuggestor == null) {
                projectServiceRegistry.register(suggestor);
            }
        });

        projects.put(projectName, project);
        return project;
    }

    public JsonNode getProjectNode(String projectName) {
        JsonNode profileNode = null;
        if (projectsNode == null) {
            projectsNode = readProjectsNode();
        }
        profileNode = projectsNode.get(projectName);
        if (profileNode == null) {
            profileNode = projectsNode.get(DEFAULT_PROJECT_KEY);
        }
        return profileNode;
    }

    public synchronized List<MetadataFieldGroup> getProjectFields(String projectName) {
        List<MetadataFieldGroup> projectFields = fields.get(projectName);
        if (projectFields == null) {
            projectFields = new ArrayList<MetadataFieldGroup>();

            final Project project = getOrCreateProject(projectName);

            final Iterable<JsonNode> iterable = () -> getProjectNode(projectName).get(METADATA_KEY).elements();

            for (JsonNode metadata : iterable) {
                String gloss = metadata.get(GLOSS_KEY) != null ? metadata.get(GLOSS_KEY).asText() : "";
                Boolean isRepeatable = metadata.get(REPEATABLE_KEY) != null ? metadata.get(REPEATABLE_KEY).asBoolean()
                        : false;
                Boolean isReadOnly = metadata.get(READ_ONLY_KEY) != null ? metadata.get(READ_ONLY_KEY).asBoolean()
                        : false;
                Boolean isHidden = metadata.get(HIDDEN_KEY) != null ? metadata.get(HIDDEN_KEY).asBoolean() : false;
                Boolean isRequired = metadata.get(REQUIRED_KEY) != null ? metadata.get(REQUIRED_KEY).asBoolean()
                        : false;
                InputType inputType = InputType
                        .valueOf(metadata.get(INPUT_TYPE_KEY) != null ? metadata.get(INPUT_TYPE_KEY).asText() : "TEXT");
                String defaultValue = metadata.get(DEFAULT_KEY) != null ? metadata.get(DEFAULT_KEY).asText() : "";

                FieldProfile fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, gloss);
                if (fieldProfile == null) {
                    fieldProfile = fieldProfileRepo.create(project, gloss, isRepeatable, isReadOnly, isHidden,
                            isRequired, inputType, defaultValue);
                }

                String labelName = metadata.get(LABEL_KEY).asText();

                MetadataFieldLabel metadataFieldLabel = metadataFieldLabelRepo.findByNameAndProfile(labelName,
                        fieldProfile);
                if (metadataFieldLabel == null) {
                    metadataFieldLabel = metadataFieldLabelRepo.create(labelName, fieldProfile);
                }

                projectFields.add(new MetadataFieldGroup(metadataFieldLabel));

                project.addProfile(fieldProfile);
            }
            fields.put(projectName, projectFields);
            projects.put(projectName, projectRepo.save(project));
        }
        return projectFields;
    }

    public synchronized void createDocument(File directory) {
        String projectName = directory.getParentFile().getName();
        String documentName = getName(directory);
        createDocument(projectName, documentName);
    }

    public synchronized void createDocument(String projectName, String documentName) {

        logger.info("Creating document " + documentName);

        if ((documentRepo.findByProjectNameAndName(projectName, documentName) == null)) {
            final Project project = getOrCreateProject(projectName);

            String documentPath = mount + "/projects/" + projectName + "/" + documentName;
            String pdfPath = documentPath + "/" + documentName + ".pdf";
            String txtPath = documentPath + "/" + documentName + ".pdf.txt";

            String pdfUri = host + pdfPath;
            String txtUri = host + txtPath;

            Document document = documentRepo.create(project, documentName, txtUri, pdfUri, txtPath, pdfPath,
                    documentPath, "Open");

            for (MetadataFieldGroup field : getProjectFields(projectName)) {
                document.addField(metadataFieldGroupRepo.create(document, field.getLabel()));
            }

            // get the Authority Beans and populate document with each Authority
            for (ProjectAuthority authority : project.getAuthorities()) {
                ((Authority) projectServiceRegistry.getService(authority.getName())).populate(document);
            }

            document = documentRepo.save(document);
            project.addDocument(document);

            try {
                simpMessagingTemplate.convertAndSend("/channel/new-document", new ApiResponse(SUCCESS, document));
            } catch (Exception e) {
                logger.error("Error broadcasting new document", e);
            }

            projects.put(projectName, projectRepo.save(project));
        }
    }

    public synchronized void createChecksum(File file) {

        String checksum = null;
        try {
            checksum = DigestUtils.md5Hex(new FileInputStream(file)) + " *" + file.getName();
            File f = new File(file.getParentFile().getAbsolutePath() + File.separator + "checksum.md5");

            Path p = Paths.get(f.getPath());

            List<String> lines = f.exists() ? Files.readAllLines(p) : new ArrayList<String>();

            if (lines.isEmpty()) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy h:mm:ssa");
                LocalDateTime now = LocalDateTime.now();
                lines.add("# MD5 Generated by MagPie (https://github.com/TAMULib/MetadataAssignmentToolUI)");
                lines.add("# Generated " + dtf.format(now));
                lines.add("");
            }

            lines.add(checksum);
            Files.write(p, lines);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void clear() {
        projects = new HashMap<String, Project>();
        fields = new HashMap<String, List<MetadataFieldGroup>>();
        projectsNode = null;
    }

    private String getName(File directory) {
        return directory.getPath().substring(directory.getParent().length() + 1);
    }

}
