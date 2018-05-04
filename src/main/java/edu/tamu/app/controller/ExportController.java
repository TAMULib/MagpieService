package edu.tamu.app.controller;

import static edu.tamu.app.Initialization.ASSETS_PATH;
import static edu.tamu.app.service.exporter.AbstractExporter.isAccepted;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.comparator.LabelComparator;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.service.exporter.DspaceCsvExporter;
import edu.tamu.app.service.exporter.SpotlightCsvExporter;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/export")
public class ExportController {

    private static final Logger logger = Logger.getLogger(ExportController.class);

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private DspaceCsvExporter dspaceCsvExporter;

    @Autowired
    private SpotlightCsvExporter spotlightCsvExporter;

    /**
     * Endpoint to return metadata headers for given project.
     * 
     * @param projectName
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/headers/{format}/{projectName}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse getMetadataHeaders(@PathVariable String projectName, @PathVariable String format) {
        List<String> metadataHeaders = null;
        switch (format) {
        case "spotlight-csv":
            metadataHeaders = spotlightCsvExporter.extractMetadataFields(projectName);
            break;

        case "dspace-csv":
            metadataHeaders = dspaceCsvExporter.extractMetadataFields(projectName);
            break;
        default:
            metadataHeaders = new ArrayList<String>();
        }
        return new ApiResponse(SUCCESS, metadataHeaders);
    }

    @RequestMapping("/spotlight-csv/{project}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse spotlightCsvExport(@PathVariable String project) {
        List<List<String>> metadata = spotlightCsvExporter.extractMetadata(projectRepo.findByName(project));
        return new ApiResponse(SUCCESS, metadata);
    }

    /**
     * Endpoint to return all published metadata fields as dspace csv by project.
     * 
     * @param project
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/dspace-csv/{project}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse dspaceCsvExport(@PathVariable String project) {
        List<List<String>> metadata = dspaceCsvExporter.extractMetadata(projectRepo.findByName(project));
        return new ApiResponse(SUCCESS, metadata);
    }

    // TODO: SAF creation should be in a service and not in the controller

    /**
     * Websocket endpoint to export saf.
     * 
     * @param project
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     * @throws FileNotFoundException
     * 
     */
    @RequestMapping("/dspace-saf/{project}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse saf(@PathVariable String project) throws IOException {

        logger.info("Generating SAF for project " + project);

        // for each published document

        Project exportableProject = projectRepo.findByName(project);
        List<Document> documents = exportableProject.getDocuments().stream().filter(isAccepted()).collect(Collectors.<Document>toList());

        String directory = ASSETS_PATH + File.separator + "exports";

        String archiveDirectoryName = directory + File.separator + project + "-" + System.currentTimeMillis();

        if (logger.isDebugEnabled()) {
            logger.debug("Archive Directory: " + archiveDirectoryName);
        }

        if (documents.size() > 0) {
            // make a containing directory for the SAF
            File safDirectory = new File(archiveDirectoryName);
            safDirectory.mkdir();
        }

        for (Document document : documents) {

            if (logger.isDebugEnabled()) {
                logger.debug("Writing archive for document " + document.getName());
            }

            // create a directory
            File itemDirectory = new File(archiveDirectoryName + File.separator + document.getName());
            itemDirectory.mkdir();

            PrintStream license = new PrintStream(itemDirectory + File.separator + "license.txt");
            license.print("The materials in this collection are hereby licensed.");
            license.flush();
            license.close();

            PrintStream manifest = new PrintStream(itemDirectory + File.separator + "contents");

            for (Resource resource : resourceRepo.findAllByDocumentProjectNameAndDocumentName(project, document.getName())) {
                FileUtils.copyFileToDirectory(new File(ASSETS_PATH + File.separator + resource.getPath()), itemDirectory);

                String bundleName = resource.getMimeType().equals("text/plain") ? "TEXT" : "ORIGINAL";
                manifest.print(resource.getName() + "\tbundle:" + bundleName + "\tprimary:true\tpermissions:-r 'member'\n");
            }

            manifest.print("license.txt\tbundle:LICENSE");
            manifest.flush();
            manifest.close();

            // for each schema in the metadata
            Map<String, PrintStream> schemaToFile = new HashMap<String, PrintStream>();

            List<MetadataFieldGroup> metadataFields = document.getFields();

            Collections.sort(metadataFields, new LabelComparator());

            for (MetadataFieldGroup metadataField : metadataFields) {
                // write a dublin-core style xml file
                String label = metadataField.getLabel().getName();
                String schema = label.split("\\.")[0];
                // System.out.println("Got schema " + schema);
                String element = label.split("\\.")[1];
                // System.out.println("Got element "+ element);

                String qualifier = null;
                if (label.split("\\.").length > 2) {
                    qualifier = label.split("\\.")[2];
                }

                if (!schemaToFile.containsKey(schema)) {
                    String filename = schema.equals("dc") ? "dublin_core.xml" : "metadata_" + schema + ".xml";
                    schemaToFile.put(schema, new PrintStream(itemDirectory + "/" + filename));
                    schemaToFile.get(schema).print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dublin_core schema=\"" + schema + "\">");
                }

                for (MetadataFieldValue metadataFieldValue : metadataField.getValues()) {
                    if (metadataFieldValue.getValue().equals(""))
                        continue;

                    schemaToFile.get(schema).print("<dcvalue element=\"" + element + "\" " + (qualifier != null ? "qualifier=\"" + qualifier + "\"" : "") + ">" + escapeForXML(metadataFieldValue.getValue()) + "</dcvalue>");
                }
            }

            for (PrintStream printStream : schemaToFile.values()) {
                printStream.print("</dublin_core>");
                printStream.close();
            }
            document.setStatus("Pending");
            document = documentRepo.update(document);

        }

        exportableProject.setLocked(true);
        projectRepo.update(exportableProject);

        return new ApiResponse(SUCCESS, "Your SAF has been written to the server filesystem at " + archiveDirectoryName + ".");
    }

    private String escapeForXML(String value) {
        value = value.replace("&", "&amp;");
        value = value.replace("\"", "&quot;");
        value = value.replace("'", "&apos;");
        value = value.replace("<", "&lt;");
        value = value.replace(">", "&gt;");
        return value;
    }

}
