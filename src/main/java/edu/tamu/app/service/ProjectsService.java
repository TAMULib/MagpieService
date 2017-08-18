package edu.tamu.app.service;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


//import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.enums.IngestType;
import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
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
    private static final String HEADLESS_KEY = "isHeadless";
    private static final String INGEST_TYPE_KEY = "ingestType";

    @Value("${app.projects.file}")
    private String initialProjectsFile;

    @Value("${app.host}")
    private String host;

    @Value("${app.mount}")
    private String mount;

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
    
    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    // TODO: initialize projects.json into database and remove this in memory
    // cache
    private JsonNode projectsNode = null;

    public JsonNode readProjectsNode() {
        String json = null;
        try {
            json = new String(Files.readAllBytes(FileSystemUtility.getWindowsSafePath(
                    resourceLoader.getResource("classpath:config").getURL().getPath() + "/" + initialProjectsFile)));
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
        }
        return project;
    }

    public Project createProject(String projectName) {

        JsonNode projectNode = getProjectNode(projectName);

        // TODO: improve the object mapping for repositories, authorities, and
        // suggestors

        List<ProjectRepository> repositories = new ArrayList<ProjectRepository>();
        if (projectNode.has(REPOSITORIES_KEY)) {
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
        }

        List<ProjectAuthority> authorities = new ArrayList<ProjectAuthority>();

        if (projectNode.has(AUTHORITIES_KEY)) {
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
        }

        List<ProjectSuggestor> suggestors = new ArrayList<ProjectSuggestor>();
        if (projectNode.has(SUGGESTORS_KEY)) {
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
        }

        Project project = projectRepo.create(projectName, repositories, authorities, suggestors);

        try {
            simpMessagingTemplate.convertAndSend("/channel/project", new ApiResponse(SUCCESS, projectRepo.findAll()));
        } catch (Exception e) {
            logger.error("Error broadcasting new project", e);
        }

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
            Boolean isRepeatable = metadata.get(REPEATABLE_KEY) != null ? metadata.get(REPEATABLE_KEY).asBoolean()
                    : false;
            Boolean isReadOnly = metadata.get(READ_ONLY_KEY) != null ? metadata.get(READ_ONLY_KEY).asBoolean() : false;
            Boolean isHidden = metadata.get(HIDDEN_KEY) != null ? metadata.get(HIDDEN_KEY).asBoolean() : false;
            Boolean isRequired = metadata.get(REQUIRED_KEY) != null ? metadata.get(REQUIRED_KEY).asBoolean() : false;
            InputType inputType = InputType
                    .valueOf(metadata.get(INPUT_TYPE_KEY) != null ? metadata.get(INPUT_TYPE_KEY).asText() : "TEXT");
            String defaultValue = metadata.get(DEFAULT_KEY) != null ? metadata.get(DEFAULT_KEY).asText() : "";

            FieldProfile fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, gloss);
            if (fieldProfile == null) {
                fieldProfile = fieldProfileRepo.create(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired,
                        inputType, defaultValue);
            }

            String labelName = metadata.get(LABEL_KEY).asText();

            MetadataFieldLabel metadataFieldLabel = metadataFieldLabelRepo.findByNameAndProfile(labelName,
                    fieldProfile);
            if (metadataFieldLabel == null) {
                metadataFieldLabel = metadataFieldLabelRepo.create(labelName, fieldProfile);
            }

            projectFields.add(new MetadataFieldGroup(metadataFieldLabel));

            if (newProject) {
                project.addProfile(fieldProfile);
            }

        }

        if (newProject) {
            projectRepo.save(project);
        }

        return projectFields;
    }

    public boolean projectIsHeadless(String projectName) {
        JsonNode projectNode = getProjectNode(projectName);
        if (projectNode.has(HEADLESS_KEY)) {
            return projectNode.get(HEADLESS_KEY).asBoolean();
        }
        return false;
    }

    public IngestType projectIngestType(String projectName) {
        IngestType type = IngestType.STANDARD;
        JsonNode projectNode = getProjectNode(projectName);
        if (projectNode.has(INGEST_TYPE_KEY)) {
            type = IngestType.valueOf(projectNode.get(INGEST_TYPE_KEY).asText());
        }
        return type;
    }

    public void createDocument(File directory) {
        createDocument(directory.getParentFile().getName(), directory.getName());
    }

    public void createSAFDocument(File directory) {
        createSAFDocument(directory.getParentFile().getName(), directory.getName());
    }

    public void createDocument(String projectName, String documentName) {
        if ((documentRepo.findByProjectNameAndName(projectName, documentName) == null)) {
            final Project project = getOrCreateProject(projectName);

            String documentPath = String.join(File.separator, mount, "projects", projectName, documentName);

            edu.tamu.app.model.Document document = documentRepo.create(project, documentName, documentPath, "Open");

            for (MetadataFieldGroup field : getProjectFields(projectName)) {
                // For headless projects, auto generate metadata
                if (projectIsHeadless(projectName)) {
                    MetadataFieldValue mfv = new MetadataFieldValue();
                    mfv.setValue(field.getLabel().getProfile().getDefaultValue());
                    MetadataFieldGroup mfg = metadataFieldGroupRepo.create(document, field.getLabel());
                    mfg.addValue(mfv);
                    document.addField(mfg);
                } else {
                    document.addField(metadataFieldGroupRepo.create(document, field.getLabel()));
                }
            }

            // get the Authority Beans and populate document with each Authority
            for (ProjectAuthority authority : project.getAuthorities()) {
                ((Authority) projectServiceRegistry.getService(authority.getName())).populate(document);
            }

            document = documentRepo.save(document);

            project.addDocument(document);

            // For headless projects, attempt to immediately push to registered
            // repositories
            if (projectIsHeadless(projectName)) {
                for (ProjectRepository repository : document.getProject().getRepositories()) {
                    try {
                        document = ((Repository) projectServiceRegistry.getService(repository.getName()))
                                .push(document);
                    } catch (IOException e) {
                        logger.error("Exception thrown attempting to push to " + repository.getName() + "!", e);
                        e.printStackTrace();
                    }
                }
            }

            try {
                simpMessagingTemplate.convertAndSend("/channel/new-document", new ApiResponse(SUCCESS, document));
            } catch (Exception e) {
                logger.error("Error broadcasting new document", e);
            }

            projectRepo.save(project);
        }
    }

    public void createSAFDocument(String projectName, String documentName) {
        if ((documentRepo.findByProjectNameAndName(projectName, documentName) == null)) {
            final Project project = getOrCreateProject(projectName);

            String documentPath = String.join(File.separator, mount, "projects", projectName, documentName);

            Document document = documentRepo.create(project, documentName, documentPath, "Open");

            // read dublin_core.xml and create the fields and their values
            // TODO: read other schemata's xml files as well
            org.w3c.dom.Document dublinCoreDocument = null;
            DocumentBuilder builder;
            try {
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                File file = resourceLoader.getResource("classpath:static" + String.join(File.separator, documentPath, "dublin_core.xml")).getFile();
                dublinCoreDocument = builder.parse(file);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (ParserConfigurationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (SAXException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            
            
            Node dublinCoreNode = dublinCoreDocument.getFirstChild();
            NodeList valueNodes = dublinCoreNode.getChildNodes();
            
            for(int i = 0; i<valueNodes.getLength(); i++) {
                System.out.println("Value Node number " + i + "...");
                Node valueNode = valueNodes.item(i);
                NamedNodeMap attributes = valueNode.getAttributes();
                String label = "";
                String value = "";
                if(attributes != null && valueNode.getNodeType() != Node.TEXT_NODE) {
                    String element = attributes.getNamedItem("element").getNodeValue();
                    String qualifier = attributes.getNamedItem("qualifier").getNodeValue();
                    System.out.println(attributes.getNamedItem("element").getNodeValue());
                    
                    label = "dc." +  element + (qualifier.equals("none")? "" : ("."+qualifier) );
                    
                    
                    FieldProfile fieldProfile = null;
                    //If the project does not have a profile to accomodate this field, make one on it
                    if(!project.hasProfileWithLabel(label)) {
                        String gloss = label;
                        //TODO:  how to determine if there are multiple values and therefore it should be repeatable?
                        Boolean isRepeatable = false;
                        Boolean isReadOnly = false;
                        Boolean isHidden = false;
                        Boolean isRequired = false;
                        InputType inputType = InputType.valueOf("TEXT");
                        String defaultValue = "";

                        fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, gloss);
                        if (fieldProfile == null) {
                            fieldProfile = fieldProfileRepo.create(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired,
                                    inputType, defaultValue);
                        }

                        MetadataFieldLabel metadataFieldLabel = metadataFieldLabelRepo.findByNameAndProfile(label,
                                fieldProfile);
                        if (metadataFieldLabel == null) {
                            metadataFieldLabel = metadataFieldLabelRepo.create(label, fieldProfile);
                        }

                        project.addProfile(fieldProfile);                        
                    } //If the project has a field profile to accommodate this field, use it 
                    else {
                        fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, label);
                        fieldProfile.setRepeatable(true);
                        fieldProfileRepo.save(fieldProfile);
                    }
                    
                    value = valueNode.getTextContent();
                    MetadataFieldLabel mfl = metadataFieldLabelRepo.findByNameAndProfile(label, fieldProfile);
                    MetadataFieldGroup mfg = metadataFieldGroupRepo.findByDocumentAndLabel(document, mfl);
                    if(mfg == null) {
                        mfg = metadataFieldGroupRepo.create(document, mfl);
                    }
                    System.out.println("Adding value \""+ value + "\" to field group " + mfg.getLabel().getName());
                    metadataFieldValueRepo.create(value, mfg);                    
                }
            }
                        

            // TODO: we could use the contents file to determine dispositions of the files, but this doesen't map clearly to Fedora
//            File contentsFile = null;
//            try {
//                contentsFile = resourceLoader.getResource("classpath:static" + String.join(File.separator, documentPath, "contents")).getFile();
//            } catch (IOException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
            
            

            try {
                simpMessagingTemplate.convertAndSend("/channel/new-document", new ApiResponse(SUCCESS, document));
            } catch (Exception e) {
                logger.error("Error broadcasting new document", e);
            }

            projectRepo.save(project);
        }
    }

    public void clear() {
        projectsNode = null;
    }

   

}
