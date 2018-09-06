package edu.tamu.app.service;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.tamu.app.exception.DocumentNotFoundException;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.service.authority.Authority;
import edu.tamu.app.service.registry.MagpieServiceRegistry;

@Service
public class DocumentFactory {

    private static final String PROJECTS_FOLDER_NAME = "projects";

    private static final Logger logger = Logger.getLogger(DocumentFactory.class);

    private final static Tika tika = new Tika();

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Autowired
    private MagpieServiceRegistry projectServiceRegistry;

    @Autowired
    private ProjectFactory projectFactory;

    public Document createDocument(File directory) throws SAXException, IOException, ParserConfigurationException {
        return createDocument(directory.getParentFile().getName(), directory.getName());
    }

    private Document createDocument(String projectName, String documentName) throws SAXException, IOException, ParserConfigurationException {
        Project project = projectRepo.findByName(projectName);
        if (project == null) {
            logger.error("Unable to find project " + projectName);
        }
        return createDocument(project, documentName);
    }

    public Document addResource(File file) throws DocumentNotFoundException {
        String documentName = file.getParentFile().getName();
        String projectName = file.getParentFile().getParentFile().getName();

        Instant start = Instant.now();
        Document document = documentRepo.findByProjectNameAndName(projectName, documentName);
        logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to lookup document while adding resource");

        if (document == null) {
            throw new DocumentNotFoundException(projectName, documentName);
        }
        return addResource(document, file);
    }

    public Document addResource(Document document, File file) {
        String resourceName = file.getName();
        String documentName = file.getParentFile().getName();
        String projectName = file.getParentFile().getParentFile().getName();
        String path = ASSETS_PATH + File.separator + document.getPath() + File.separator + file.getName();
        String mimeType = tika.detect(path);

        Instant start = Instant.now();
        Resource resource = resourceRepo.findByDocumentProjectNameAndDocumentNameAndName(projectName, documentName, resourceName);
        logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to look for existing resource while adding resource");

        if (resource == null) {
            logger.info("Adding new resource " + resourceName + " - " + mimeType + " for document " + document.getName());

            start = Instant.now();
            resourceRepo.create(document, resourceName, path, mimeType);
            logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to create new resource");

            start = Instant.now();
            document = documentRepo.findByProjectNameAndName(projectName, documentName);
            logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to lookup document after creating new resource");

        } else {
            logger.info("Resource " + resourceName + " already exists for document " + documentName);
        }
        return document;
    }

    private Document createDocument(Project project, String documentName) throws SAXException, IOException, ParserConfigurationException {
        Document document;
        switch (project.getIngestType()) {
        case SAF:
            document = createSAFDocument(project, documentName);
            break;
        case STANDARD:
        default:
            document = createStandardDocument(project, documentName);
            break;
        }
        return document;
    }

    private Document createStandardDocument(Project project, String documentName) {
        String projectName = project.getName();
        String documentPath = getDocumentPath(projectName, documentName);
        logger.info("Creating standard document at " + documentPath);

        Instant start = Instant.now();
        Document document = documentRepo.create(project, documentName, documentPath, "Open");
        logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to create new document");

        start = Instant.now();
        document = addMetadataFields(document, project.getName());
        logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to add metadata to new document");

        start = Instant.now();
        document = applyAuthorities(document, project.getAuthorities());
        logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to apply authorities to new document");

        start = Instant.now();
        document = documentRepo.update(document);
        logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to update new document");

        project.addDocument(document);

        start = Instant.now();
        projectRepo.update(project);
        logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to update project");

        return document;
    }

    private String getDocumentPath(String projectName, String documentName) {
        return String.join(File.separator, ASSETS_PATH, PROJECTS_FOLDER_NAME, projectName, documentName);
    }

    private Document addMetadataFields(Document document, String projectName) {
        for (MetadataFieldGroup field : projectFactory.getProjectFields(projectName)) {
            logger.info("Adding field " + field.getLabel().getName() + " to document " + document.getName());
            Instant start = Instant.now();
            document.addField(metadataFieldGroupRepo.create(document, field.getLabel()));
            logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to create new metadata field group");
        }
        return document;
    }

    private Document applyAuthorities(Document document, List<ProjectAuthority> authorities) {
        for (ProjectAuthority authority : authorities) {
            logger.info("Applying authority " + authority.getName() + " to " + document.getName());
            Instant start = Instant.now();
            document = ((Authority) projectServiceRegistry.getService(authority.getName())).populate(document);
            logger.info(Duration.between(start, Instant.now()).toMillis() + " milliseconds to apply authority " + authority.getName());
        }
        return document;
    }

    private Document createSAFDocument(Project project, String documentName) throws SAXException, IOException, ParserConfigurationException {

        String documentPath = getDocumentPath(project.getName(), documentName);

        logger.info("Creating SAF document at " + documentPath);

        Document document = documentRepo.create(project, documentName, documentPath, "Open");

        Long docId = document.getId();

        // read dublin_core.xml and create the fields and their values
        // TODO: read other schemata's xml files as well

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        File dublinCoreFile = new File(String.join(File.separator, documentPath, "dublin_core.xml"));
        org.w3c.dom.Document dublinCoreDocument = builder.parse(dublinCoreFile);

        Node dublinCoreNode = dublinCoreDocument.getFirstChild();
        NodeList valueNodes = dublinCoreNode.getChildNodes();

        for (int i = 0; i < valueNodes.getLength(); i++) {
            Node valueNode = valueNodes.item(i);
            NamedNodeMap attributes = valueNode.getAttributes();
            String label = "";
            String value = "";
            if (attributes != null && valueNode.getNodeType() != Node.TEXT_NODE) {

                String element = attributes.getNamedItem("element").getNodeValue();
                String qualifier = attributes.getNamedItem("qualifier").getNodeValue();
                label = "dc." + element + (qualifier.equals("none") ? "" : ("." + qualifier));

                FieldProfile fieldProfile = null;
                // If the project does not have a profile to accommodate
                // this field, make one on it
                if (!project.hasProfileWithLabel(label)) {
                    String gloss = label;
                    // TODO: how to determine if there are multiple values
                    // and therefore it should be repeatable?
                    Boolean isRepeatable = false;
                    Boolean isReadOnly = false;
                    Boolean isHidden = false;
                    Boolean isRequired = false;
                    InputType inputType = InputType.valueOf("TEXT");
                    String defaultValue = "";

                    fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, gloss);
                    if (fieldProfile == null) {
                        fieldProfile = fieldProfileRepo.create(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue);
                    }

                    MetadataFieldLabel metadataFieldLabel = metadataFieldLabelRepo.findByNameAndProfile(label, fieldProfile);
                    if (metadataFieldLabel == null) {
                        metadataFieldLabel = metadataFieldLabelRepo.create(label, fieldProfile);
                    }

                    project.addProfile(fieldProfile);

                    project = projectRepo.save(project);

                }
                // If the project has a field profile to accommodate this
                // field, use it
                else {
                    fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, label);
                }

                value = valueNode.getTextContent();
                MetadataFieldLabel mfl = metadataFieldLabelRepo.findByNameAndProfile(label, fieldProfile);
                MetadataFieldGroup mfg = metadataFieldGroupRepo.findByDocumentAndLabel(document, mfl);

                // if the document doesn't have a field group for this
                // field, then create it
                if (mfg == null) {
                    mfg = metadataFieldGroupRepo.create(document, mfl);

                    mfg.setDocument(document);

                    mfg = metadataFieldGroupRepo.save(mfg);

                    document = documentRepo.findOne(docId);

                }
                // otherwise, note that the field is being repeated on this
                // document (and therefore for the project as a whole)
                else {
                    fieldProfile.setRepeatable(true);
                    fieldProfile = fieldProfileRepo.save(fieldProfile);
                }

                MetadataFieldValue mfv = metadataFieldValueRepo.create(value, mfg);

                mfg.addValue(mfv);

                mfg = metadataFieldGroupRepo.save(mfg);

                document.addField(mfg);

                document = documentRepo.save(document);
            }
        }

        // use the contents file to determine content files
        // TODO: this throws away a lot of the details of the contents
        // manifest, but it's unclear how to map it all to Fedora anyway
        File contentsFile = null;
        try {
            contentsFile = new File(String.join(File.separator, documentPath, "contents"));
            String line = "";

            if (contentsFile.exists()) {
                BufferedReader is = new BufferedReader(new FileReader(contentsFile));

                while ((line = is.readLine()) != null) {
                    logger.info("Got contents line: " + line);
                    if ("".equals(line.trim()) || line.contains("bundle:THUMBNAIL")) {
                        continue;
                    }

                    String filename = line.split("\\t")[0];

                    String filePath = ASSETS_PATH + document.getPath() + File.separator + filename;

                    File file = new File(filePath);

                    logger.info("Attempting to add file from contents: " + filePath);
                    if (file.exists() && file.isFile()) {
                        String name = file.getName();
                        String path = ASSETS_PATH + document.getPath() + File.separator + file.getName();
                        String mimeType = tika.detect(path);
                        logger.info("Adding resource " + name + " - " + mimeType + " to document " + document.getName());
                        resourceRepo.create(document, name, path, mimeType);
                    } else {
                        logger.warn("Could not find file " + file.getPath());
                    }

                }
                documentRepo.save(document);
                is.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        documentRepo.broadcast(document);

        projectRepo.update(project);
        return document;
    }

}
