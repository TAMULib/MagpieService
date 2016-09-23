package edu.tamu.app.service;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectProfile;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.response.marc.FlatMARC;
import edu.tamu.framework.model.ApiResponse;

@Component
@Scope("prototype")
public class ProjectFileListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(ProjectFileListener.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private VoyagerService voyagerService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Autowired
    private ProjectProfileRepo projectLabelProfileRepo;

    @Value("${app.host}")
    private String host;

    private Project project;

    private List<MetadataFieldGroup> fields;

    private JsonNode projectNode;

    private JsonNode profileNode;

    public ProjectFileListener(String root, String folder) {
        this.root = root;
        this.folder = folder;
    }

    private void readProjectNode() {
        String json = null;
        try {
            json = new String(Files.readAllBytes(Paths.get(appContext.getResource("classpath:config").getFile().getAbsolutePath() + "/metadata.json")));
        } catch (IOException e) {
            logger.error("Error reading metadata json file", e);
        }
        if (json != null) {
            try {
                projectNode = objectMapper.readTree(json);
            } catch (Exception e) {
                logger.error("Error reading the metadata json with the Object Mapper", e);
            }
        }
    }

    private void createProject(File directory) {

        if (projectNode == null) {
            readProjectNode();
        }

        String name = getName(directory);

        logger.info("Creating project " + name);

        project = projectRepo.findByName(name);

        if (project == null) {
            project = projectRepo.create(name);
        }

        profileNode = projectNode.get(name);

        if (profileNode == null) {
            profileNode = projectNode.get("default");
        }

        fields = new ArrayList<MetadataFieldGroup>();

        final Iterable<JsonNode> iterable = () -> profileNode.elements();
        iterable.forEach(metadata -> {
            ProjectProfile profile = projectLabelProfileRepo.create(project, metadata.get("gloss") == null ? "" : metadata.get("gloss").asText(), metadata.get("repeatable") == null ? false : metadata.get("repeatable").asBoolean(), metadata.get("readOnly") == null ? false : metadata.get("readOnly").asBoolean(), metadata.get("hidden") == null ? false : metadata.get("hidden").asBoolean(), metadata.get("required") == null ? false : metadata.get("required").asBoolean(), InputType.valueOf(metadata.get("gloss") == null ? "TEXT" : metadata.get("inputType").asText()), metadata.get("default") == null ? "" : metadata.get("default").asText());
            MetadataFieldLabel label = metadataFieldLabelRepo.create(metadata.get("label").asText(), profile);
            fields.add(new MetadataFieldGroup(label));
            project.addProfile(profile);
        });
        projectRepo.save(project);
    }

    @SuppressWarnings("unchecked")
    private void createDocument(File directory) {
        String name = getName(directory);

        logger.info("Creating document " + name);

        if ((documentRepo.findByName(name) == null)) {

            String pdfPath = directory.getPath() + "/" + name + ".pdf";
            String txtPath = directory.getPath() + "/" + name + ".txt";

            String pdfUri = host + pdfPath;
            String txtUri = host + txtPath;

            Document document = documentRepo.create(project, name, txtUri, pdfUri, txtPath, pdfPath, "Open");

            fields.parallelStream().forEach(field -> {
                document.addField(metadataFieldGroupRepo.create(document, field.getLabel()));
            });

            FlatMARC flatMarc = null;
            try {
                flatMarc = new FlatMARC(voyagerService.getMARC(document.getName()));

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

                document.getFields().parallelStream().forEach(field -> {
                    List<String> values = metadataMap.get(field.getLabel().getName());
                    if (values != null) {
                        values.forEach(value -> {
                            field.addValue(metadataFieldValueRepo.create(value, field));
                        });
                    }
                });

            } catch (Exception e) {
                logger.debug("MARC record does not exist: " + name);
            }

            project.addDocument(documentRepo.save(document));

            Map<String, Object> docMap = new HashMap<String, Object>();
            docMap.put("document", document);
            docMap.put("isNew", "true");

            try {
                simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse(SUCCESS, docMap));
            } catch (Exception e) {
                logger.error("Error broadcasting new document", e);
            }

            projectRepo.save(project);
        }
    }

    private String getName(File directory) {
        return directory.getPath().substring(directory.getParent().length() + 1);
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
    }

    @Override
    public void onDirectoryCreate(File directory) {
        if (directory.getParent().equals(getPath())) {
            createProject(directory);
        } else {
            createDocument(directory);
        }
    }

    @Override
    public void onDirectoryChange(File directory) {
    }

    @Override
    public void onDirectoryDelete(File directory) {
    }

    @Override
    public void onFileCreate(File file) {
    }

    @Override
    public void onFileChange(File file) {
    }

    @Override
    public void onFileDelete(File file) {
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
    }

}
