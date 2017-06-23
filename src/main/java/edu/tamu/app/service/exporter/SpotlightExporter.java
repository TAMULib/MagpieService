package edu.tamu.app.service.exporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import edu.tamu.app.enums.ServiceType;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.PublishedLocation;

@Service
public class SpotlightExporter extends AbstractExporter {

    private static final String SPOTLIGHT_REPOSITORY_NAME = "Fedora for Spotlight Exhibits";

    @Override
    public List<List<String>> extractMetadata(Project project) {

        List<List<String>> metadata = new ArrayList<List<String>>();

        project.getDocuments().stream().filter(isPublished()).collect(Collectors.<Document>toList()).forEach(document -> {

            List<MetadataFieldGroup> metadataFields = document.getFields();

            Collections.sort(metadataFields, new LabelComparator());

            List<String> documentMetadata = new ArrayList<String>();

            String publishedUrl = getPublishedUrl(document);

            documentMetadata.add(0, publishedUrl);

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

    private String getPublishedUrl(Document document) {
        Optional<String> publishedUrl = Optional.empty();

        for (PublishedLocation publishedLocation : document.getPublishedLocations()) {
            if (publishedLocation.getRepository().getType().equals(ServiceType.FEDORA_SPOTLIGHT) && publishedLocation.getRepository().getName().equals(SPOTLIGHT_REPOSITORY_NAME)) {
                publishedUrl = Optional.of(publishedLocation.getUrl());
                break;
            }
        }

        if (!publishedUrl.isPresent()) {
            throw new RuntimeException("Could not find Fedora Spotlight published url for document " + document.getName());
        }

        return publishedUrl.get();
    }

    @Override
    public List<String> extractMetadataFields(String projectName) {

        Project project = projectRepo.findByName(projectName);

        List<String> metadataHeaders = performMetadataFieldsExtraction(project);

        Collections.sort(metadataHeaders);

        metadataHeaders.add(0, "url");

        return metadataHeaders;
    }

}
