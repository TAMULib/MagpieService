package edu.tamu.app.service.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.app.comparator.LabelComparator;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.ResourceRepo;

@Service
public class DspaceCsvExporter extends AbstractExporter {

    @Autowired
    private ResourceRepo resourceRepo;

    @Override
    public List<List<String>> extractMetadata(Project project) {

        List<List<String>> metadata = new ArrayList<List<String>>();

        project.getDocuments().stream().filter(isAccepted()).collect(Collectors.<Document>toList()).forEach(document -> {

            List<MetadataFieldGroup> metadataFields = document.getFields();

            Collections.sort(metadataFields, new LabelComparator());

            List<String> documentMetadata = new ArrayList<String>();

            documentMetadata.add(resourceRepo.findAllByDocumentProjectNameAndDocumentNameAndMimeType(document.getProject().getName(), document.getName(), "application/pdf").get(0).getName());

            metadataFields.forEach(field -> {
                String values = null;
                boolean firstPass = true;
                for (MetadataFieldValue medataFieldValue : field.getValues()) {
                    if (firstPass) {
                        values = medataFieldValue.getValue();
                        firstPass = false;
                    } else {
                        values += "||" + medataFieldValue.getValue();
                    }
                }
                documentMetadata.add(values);
            });

            metadata.add(documentMetadata);

        });

        return metadata;

    }

    @Override
    public List<String> extractMetadataFields(String projectName) {

        Project project = projectRepo.findByName(projectName);

        List<String> metadataHeaders = performMetadataFieldsExtraction(project);

        metadataHeaders.add("bundle:ORIGINAL");

        Collections.sort(metadataHeaders);

        System.out.println("SORTED LIST:" + metadataHeaders);

        return metadataHeaders;
    }

}
