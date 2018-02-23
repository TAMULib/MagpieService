package edu.tamu.app.service;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

import edu.tamu.app.Initialization;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.service.authority.Authority;
import edu.tamu.app.service.registry.MagpieServiceRegistry;
import edu.tamu.app.service.repository.Repository;

@Service
public class DocumentFactory {

    private static final Logger logger = Logger.getLogger(DocumentFactory.class);

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

    private static Tika tika = new Tika();

    public Document getOrCreateDocument(File directory) throws SAXException, IOException, ParserConfigurationException {
        return getOrCreateDocument(directory.getParentFile().getName(), directory.getName());
    }

    public Document getOrCreateDocument(String projectName, String documentName) throws SAXException, IOException, ParserConfigurationException {
        Document document = documentRepo.findByProjectNameAndName(projectName, documentName);
        if (document == null) {
            Project project = projectRepo.findByName(projectName);
            document = createDocument(project, documentName);
        }
        return document;
    }

    public void addResource(Document document, File file) {
        String name = file.getName();
        String path = document.getDocumentPath() + File.separator + file.getName();
        String mimeType = tika.detect(path);
        logger.info("Adding resource " + name + " - " + mimeType + " to document " + document.getName());
        resourceRepo.create(document, name, path.replace(ASSETS_PATH, ""), mimeType);
    }

    public Document createDocument(Project project, String documentName) throws SAXException, IOException, ParserConfigurationException {
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

        String documentPath = String.join(File.separator, Initialization.ASSETS_PATH, "projects", project.getName(), documentName);

        edu.tamu.app.model.Document document = documentRepo.create(project, documentName, documentPath, "Open");

        for (MetadataFieldGroup field : projectFactory.getProjectFields(project.getName())) {
            // For headless projects, auto generate metadata
            if (project.isHeadless()) {
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

        // For headless projects, attempt to immediately push to registered repositories
        if (project.isHeadless()) {
            for (ProjectRepository repository : document.getProject().getRepositories()) {
                try {
                    document = ((Repository) projectServiceRegistry.getService(repository.getName())).push(document);
                } catch (IOException e) {
                    logger.error("Exception thrown attempting to push to " + repository.getName() + "!", e);
                    e.printStackTrace();
                }
            }
        }

        documentRepo.broadcast(document);

        projectRepo.update(project);
        return document;
    }

    private Document createSAFDocument(Project project, String documentName) throws SAXException, IOException, ParserConfigurationException {

        String documentPath = String.join(File.separator, Initialization.ASSETS_PATH, "projects", project.getName(), documentName);

        Document document = documentRepo.create(project, documentName, documentPath, "Open");

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
                }
                // otherwise, note that the field is being repeated on this
                // document (and therefore for the project as a whole)
                else {
                    fieldProfile.setRepeatable(true);
                    fieldProfile = fieldProfileRepo.save(fieldProfile);
                }

                metadataFieldValueRepo.create(value, mfg);
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

                    String filePath = document.getDocumentPath() + File.separator + filename;

                    File file = new File(filePath);

                    logger.info("Attempting to add file from contents: " + filePath);
                    if (file.exists() && file.isFile()) {
                        String name = file.getName();
                        String path = document.getDocumentPath() + File.separator + file.getName();
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
