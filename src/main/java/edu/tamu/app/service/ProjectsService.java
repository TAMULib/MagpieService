package edu.tamu.app.service;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import edu.tamu.app.authority.Authority;
import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.utilities.FileSystemUtility;
import edu.tamu.framework.SpringContext;
import edu.tamu.framework.model.ApiResponse;

@Service
public class ProjectsService {

    private static final Logger logger = Logger.getLogger(ProjectsService.class);

    private static final String DEFAULT_PROJECT_KEY = "default";
    private static final String METADATA_KEY = "metadata";
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
            json = new String(Files.readAllBytes(FileSystemUtility.getWindowsSafePath(resourceLoader.getResource("classpath:config").getURL().getPath() + "/projects.json")));
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

    public synchronized Project getProject(String projectName) {
        Project project = projects.get(projectName);
        if (project == null) {
            project = projectRepo.findByName(projectName);
        }
        if (project == null) {

            JsonNode projectNode = getProjectNode(projectName);

            Set<String> authorities = new HashSet<String>();

            try {
                authorities = objectMapper.readValue(projectNode.get(AUTHORITIES_KEY).toString(), new TypeReference<Set<String>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<String> suggestors = new HashSet<String>();

            try {
                suggestors = objectMapper.readValue(projectNode.get(SUGGESTORS_KEY).toString(), new TypeReference<Set<String>>() {
                });
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            project = projectRepo.create(projectName, authorities, suggestors);

            try {
                simpMessagingTemplate.convertAndSend("/channel/project", new ApiResponse(SUCCESS, projectRepo.findAll()));
            } catch (Exception e) {
                logger.error("Error broadcasting new project", e);
            }
        }
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

            final Project project = getProject(projectName);

            final Iterable<JsonNode> iterable = () -> getProjectNode(projectName).get(METADATA_KEY).elements();

            for (JsonNode metadata : iterable) {
                String gloss = metadata.get(GLOSS_KEY) == null ? "" : metadata.get(GLOSS_KEY).asText();
                Boolean isRepeatable = metadata.get(REPEATABLE_KEY) == null ? false : metadata.get(REPEATABLE_KEY).asBoolean();
                Boolean isReadOnly = metadata.get(READ_ONLY_KEY) == null ? false : metadata.get(READ_ONLY_KEY).asBoolean();
                Boolean isHidden = metadata.get(HIDDEN_KEY) == null ? false : metadata.get(HIDDEN_KEY).asBoolean();
                Boolean isRequired = metadata.get(REQUIRED_KEY) == null ? false : metadata.get(REQUIRED_KEY).asBoolean();
                InputType inputType = InputType.valueOf(metadata.get(INPUT_TYPE_KEY) == null ? "TEXT" : metadata.get(INPUT_TYPE_KEY).asText());
                String defaultValue = metadata.get(DEFAULT_KEY) == null ? "" : metadata.get(DEFAULT_KEY).asText();

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

                project.addProfile(fieldProfile);
            }
            fields.put(projectName, projectFields);
            projects.put(projectName, projectRepo.save(project));
        }
        return projectFields;
    }

    public synchronized void createDocument(String projectName, String documentName) {

        if ((documentRepo.findByProjectNameAndName(projectName, documentName) == null)) {
            final Project project = getProject(projectName);

            String pdfPath = mount + "/projects/" + projectName + "/" + documentName + "/" + documentName + ".pdf";
            String txtPath = mount + "/projects/" + projectName + "/" + documentName + "/" + documentName + ".pdf.txt";

            String pdfUri = host + pdfPath;
            String txtUri = host + txtPath;

            Document document = documentRepo.create(project, documentName, txtUri, pdfUri, txtPath, pdfPath, "Open");

            for (MetadataFieldGroup field : getProjectFields(projectName)) {
                document.addField(metadataFieldGroupRepo.create(document, field.getLabel()));
            }

            // get the Authority Beans and populate document with each Authority
            for (String authority : project.getAuthorities()) {
                document = ((Authority) SpringContext.bean(authority)).populate(document);
            }
            
            document = documentRepo.save(document);
            project.addDocument(document);

            try {
                simpMessagingTemplate.convertAndSend("/channel/document", new ApiResponse(SUCCESS, document));
            } catch (Exception e) {
                logger.error("Error broadcasting new document", e);
            }

            projects.put(projectName, projectRepo.save(project));
        }
    }

    public void clear() {
        projects = new HashMap<String, Project>();
        fields = new HashMap<String, List<MetadataFieldGroup>>();
        projectsNode = null;
    }

}
