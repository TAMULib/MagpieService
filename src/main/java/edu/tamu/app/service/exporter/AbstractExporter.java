package edu.tamu.app.service.exporter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.service.ProjectFactory;

public abstract class AbstractExporter implements Exporter {

    @Autowired
    protected ProjectRepo projectRepo;

    @Autowired
    protected ProjectFactory projectsService;

    public static Predicate<Document> isPublished() {
        return d -> d.getStatus().equals("Published");
    }

    public static Predicate<Document> isAccepted() {
        return d -> d.getStatus().equals("Accepted");
    }

    public static Predicate<Document> isPending() {
        return d -> d.getStatus().equals("Pending");
    }

    protected List<String> performMetadataFieldsExtraction(Project project) {
        List<String> metadataHeaders = new ArrayList<String>();
        projectsService.getProjectFields(project.getName()).forEach(mfg -> {
            metadataHeaders.add(mfg.getLabel().getName());
        });
        return metadataHeaders;
    }

}
