package edu.tamu.app.service;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.response.marc.FlatMARC;
import edu.tamu.app.observer.ProjectFileListener;
import edu.tamu.framework.model.ApiResponse;

@Service
public class ProjectsService {

    private static final Logger logger = Logger.getLogger(ProjectFileListener.class);

    private static final String DEFAULT = "default";

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private VoyagerService voyagerService;

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

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Value("${app.host}")
    private String host;

    @Value("${app.mount}")
    private String mount;

    private Map<String, Project> projects = new HashMap<String, Project>();

    private JsonNode projectsNode;

    public JsonNode readProjectNode() {
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get(resourceLoader.getResource("classpath:config").getURL().getPath() + "/metadata.json")));
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

    public Project createProject(String projectName) {
        Project project = projects.get(projectName);
        if (project == null) {
            project = projectRepo.findByName(projectName);
        }
        if (project == null) {
            project = projectRepo.create(projectName);
            try {
                simpMessagingTemplate.convertAndSend("/channel/project", new ApiResponse(SUCCESS, projectRepo.findAll()));
            } catch (Exception e) {
                logger.error("Error broadcasting new project", e);
            }
        }
        projects.put(projectName, project);
        return project;
    }

    public JsonNode getProfileNode(String projectName) {
        JsonNode profileNode = null;
        if (projectsNode == null) {
            projectsNode = readProjectNode();
        }
        profileNode = projectsNode.get(projectName);
        if (profileNode == null) {
            profileNode = projectsNode.get(DEFAULT);
        }
        return profileNode;
    }

    public List<MetadataFieldGroup> getProjectFields(String projectName) {
        List<MetadataFieldGroup> fields = new ArrayList<MetadataFieldGroup>();
        final Project project = createProject(projectName);
        final Iterable<JsonNode> iterable = () -> getProfileNode(projectName).elements();
        iterable.forEach(metadata -> {

            String gloss = metadata.get("gloss") == null ? "" : metadata.get("gloss").asText();
            Boolean isRepeatable = metadata.get("repeatable") == null ? false : metadata.get("repeatable").asBoolean();
            Boolean isReadOnly = metadata.get("readOnly") == null ? false : metadata.get("readOnly").asBoolean();
            Boolean isHidden = metadata.get("hidden") == null ? false : metadata.get("hidden").asBoolean();
            Boolean isRequired = metadata.get("required") == null ? false : metadata.get("required").asBoolean();
            InputType inputType = InputType.valueOf(metadata.get("inputType") == null ? "TEXT" : metadata.get("inputType").asText());
            String defaultValue = metadata.get("default") == null ? "" : metadata.get("default").asText();

            FieldProfile fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, gloss);
            if (fieldProfile == null) {
                fieldProfile = fieldProfileRepo.create(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue);
            }

            String label = metadata.get("label").asText();

            MetadataFieldLabel metadataFieldLabel = metadataFieldLabelRepo.findByName(label);
            if (metadataFieldLabel == null) {
                metadataFieldLabel = metadataFieldLabelRepo.create(label, fieldProfile);
            }

            fields.add(new MetadataFieldGroup(metadataFieldLabel));

            project.addProfile(fieldProfile);
        });
        projects.put(projectName, projectRepo.save(project));
        return fields;
    }

    public void createDocument(String projectName, String documentName) {

        if ((documentRepo.findByName(documentName) == null)) {
            final Project project = createProject(projectName);

            String pdfPath = mount + "/projects/" + projectName + "/" + documentName + "/" + documentName + ".pdf";
            String txtPath = mount + "/projects/" + projectName + "/" + documentName + "/" + documentName + ".pdf.txt";

            String pdfUri = host + pdfPath;
            String txtUri = host + txtPath;

            Document document = documentRepo.create(project, documentName, txtUri, pdfUri, txtPath, pdfPath, "Open");

            getProjectFields(projectName).parallelStream().forEach(field -> {
                document.addField(metadataFieldGroupRepo.create(document, field.getLabel()));
            });

            try {
                Map<String, List<String>> metadataMap = getMARCRecordMetadata(document.getName());

                document.getFields().parallelStream().forEach(field -> {
                    List<String> values = metadataMap.get(field.getLabel().getName());
                    if (values != null) {
                        values.forEach(value -> {
                            field.addValue(metadataFieldValueRepo.create(value, field));
                        });
                    }
                });

            } catch (Exception e) {
                logger.debug("MARC record does not exist: " + documentName);
            }

            project.addDocument(documentRepo.save(document));

            try {
                simpMessagingTemplate.convertAndSend("/channel/document", new ApiResponse(SUCCESS));
            } catch (Exception e) {
                logger.error("Error broadcasting new document", e);
            }

            projects.put(projectName, projectRepo.save(project));
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getMARCRecordMetadata(String documentName) throws Exception {
        FlatMARC flatMarc = new FlatMARC(voyagerService.getMARC(documentName));

        Field[] marcFields = FlatMARC.class.getDeclaredFields();

        Map<String, List<String>> metadataMap = new HashMap<String, List<String>>();

        for (Field field : marcFields) {
            field.setAccessible(true);
            List<String> marcList = new ArrayList<String>();
            if (field.getGenericType().toString().equals("java.util.List<java.lang.String>")) {
                try {
                    for (String string : (List<String>) field.get(flatMarc)) {
                        marcList.add(string);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Illegal Argument", e);
                } catch (IllegalAccessException e) {
                    logger.error("Illegal Access", e);
                }
            } else {
                try {
                    marcList.add(field.get(flatMarc).toString());
                } catch (IllegalArgumentException e) {
                    logger.error("Illegal Argument", e);
                } catch (IllegalAccessException e) {
                    logger.error("Illegal Access", e);
                }
            }

            metadataMap.put(field.getName().replace('_', '.'), marcList);
        }

        return metadataMap;
    }

}
