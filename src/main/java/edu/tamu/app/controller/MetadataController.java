/* 
 * MetadataFieldController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.exporter.DspaceCSVExporter;
import edu.tamu.app.service.exporter.SpotlightExporter;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiVariable;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Metadata Field Controller
 * 
 * @author
 *
 */
@RestController
@ApiMapping("/metadata")
public class MetadataController {

    @Value("${app.mount}")
    private String mount;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private DocumentRepo documentRepo;
    
    
    @Autowired
    private DspaceCSVExporter dspaceCSVExporter;
    
    @Autowired
    private SpotlightExporter spotlightExporter;

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger logger = Logger.getLogger(MetadataController.class);

    /**
     * Endpoint to unlock a given project
     * 
     * @param projectToUnlock
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/unlock/{projectToUnlock}")
    @Auth(role = "ROLE_USER")
    public ApiResponse unlockProject(@ApiVariable String projectToUnlock) {
        Project project = projectRepo.findByName(projectToUnlock);
        project.setIsLocked(false);
        projectRepo.save(project);
        return new ApiResponse(SUCCESS);
    }

    /**
     * Endpoint to return metadata headers for given project.
     * 
     * @param projectName
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/headers/{format}/{projectName}")
    @Auth(role = "ROLE_USER")
    public ApiResponse getMetadataHeaders(@ApiVariable String projectName, @ApiVariable String format) {
    	List<String> metadataHeaders = null;
    	
    	switch(format) {
    	
	    	case "spotlight":
	    		metadataHeaders = spotlightExporter.extractMetadataFields(projectName);
	    	break;
	    	
	    	case "dspacecsv":
	    		metadataHeaders = dspaceCSVExporter.extractMetadataFields(projectName);
	    	break;
	    	
	    	default: metadataHeaders = new ArrayList<String>();
    	
    	}
    	        
        return new ApiResponse(SUCCESS, metadataHeaders);
    }
    
    @ApiMapping("/spotlight/{project}")
    @Auth(role = "ROLE_USER")
    public ApiResponse getSpotlightExport(@ApiVariable String project) {
    	
    	List<List<String>> metadata = spotlightExporter.extractMetadata(projectRepo.findByName(project));

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
    @ApiMapping("/dspacecsv/{project}")
    @Auth(role = "ROLE_USER")
    public ApiResponse getCSVByroject(@ApiVariable String project) {

        List<List<String>> metadata = dspaceCSVExporter.extractMetadata(projectRepo.findByName(project));
        
        return new ApiResponse(SUCCESS, metadata);
    }

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
    @ApiMapping("/saf/{project}")
    @Auth(role = "ROLE_USER")
    public ApiResponse saf(@ApiVariable String project) throws FileNotFoundException {

        logger.info("Generating SAF for project " + project);

        // for each published document

        Project exportableProject = projectRepo.findByName(project);
        List<Document> documents = exportableProject.getDocuments().stream().filter(isAccepted()).collect(Collectors.<Document>toList());

        String directory = "";
        try {
            directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/exports/";
        } catch (IOException e) {
            logger.error("Error building exports directory", e);
            return new ApiResponse(ERROR, "Error building exports directory");
        }

        String archiveDirectoryName = directory + project + System.currentTimeMillis();

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
            File itemDirectory = new File(archiveDirectoryName + "/" + document.getName());
            itemDirectory.mkdir();

            File originDir = null;
            try {
                String documentDirectory = appContext.getResource("classpath:static" + document.getPdfPath()).getFile().getAbsolutePath();
                documentDirectory = documentDirectory.substring(0, documentDirectory.length() - (document.getName().length() + 5));
                originDir = new File(documentDirectory);
                FileUtils.copyDirectory(originDir, itemDirectory);
            } catch (IOException e) {
                logger.error("Error copying document directory", e);
                return new ApiResponse(ERROR, "Error copying document directory");
            }

            PrintStream license = new PrintStream(itemDirectory + "/license.txt");
            license.print("The materials in this collection are hereby licensed.");
            license.flush();
            license.close();

            PrintStream manifest = new PrintStream(itemDirectory + "/contents");
            for (File file : originDir.listFiles()) {
                // if the file is of type txt, put it in the TEXT bundle.
                // Otherwise, ORIGINAL is the place to put it.
                String bundleName = file.getName().endsWith("txt") ? "TEXT" : "ORIGINAL";
                manifest.print(file.getName() + "\tbundle:" + bundleName + "\tprimary:true\tpermissions:-r 'member'\n");
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
            document = documentRepo.save(document);

            Map<String, Object> documentMap = new HashMap<String, Object>();

            documentMap.put("document", document);
            documentMap.put("isNew", "false");

            simpMessagingTemplate.convertAndSend("/channel/documents", new ApiResponse(SUCCESS, documentMap));
        }
        exportableProject.setIsLocked(true);
        projectRepo.save(exportableProject);
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

    /**
     * Endpoint to return all by status metadata fields.
     * 
     * @param status
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/status/{status}")
    @Auth(role = "ROLE_USER")
    public ApiResponse published(@ApiVariable String status) {

        List<List<String>> metadata = new ArrayList<List<String>>();

        documentRepo.findByStatus(status).forEach(document -> {

            new TreeSet<MetadataFieldGroup>(document.getFields()).forEach(field -> {

                field.getValues().forEach(value -> {

                    List<String> documentMetadata = new ArrayList<String>();

                    documentMetadata.add(field.getLabel().getName());
                    documentMetadata.add(value.getValue());

                    metadata.add(documentMetadata);

                });

            });

        });

        return new ApiResponse(SUCCESS, metadata);
    }

    /**
     * Endpoint to return all metadata fields.
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_USER")
    public ApiResponse all() {
        Map<String, List<MetadataFieldGroup>> metadataMap = new HashMap<String, List<MetadataFieldGroup>>();
        metadataMap.put("list", metadataFieldGroupRepo.findAll());
        return new ApiResponse(SUCCESS, metadataMap);
    }

    public static Predicate<Document> isPublished() {
        return d -> d.getStatus().equals("Published");
    }

    public static Predicate<Document> isAccepted() {
        return d -> d.getStatus().equals("Accepted");
    }

    public static Predicate<Document> isPending() {
        return d -> d.getStatus().equals("Pending");
    }

    /**
     * Class for comparing MetadataFieldImpl by label.
     * 
     * @author
     *
     */
    class LabelComparator implements Comparator<MetadataFieldGroup> {
        /**
         * Compare labels of MetadataFieldImpl
         * 
         * @param mfg1
         *            MetadataFieldGroup
         * @param mfg2
         *            MetadataFieldGroup
         * 
         * @return int
         */
        @Override
        public int compare(MetadataFieldGroup mfg1, MetadataFieldGroup mfg2) {
            return mfg1.getLabel().getName().compareTo(mfg2.getLabel().getName());
        }
    }

}
