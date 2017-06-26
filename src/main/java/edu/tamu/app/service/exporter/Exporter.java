package edu.tamu.app.service.exporter;

import java.util.List;

import edu.tamu.app.model.Project;

public interface Exporter {

    public List<List<String>> extractMetadata(Project project);

    public List<String> extractMetadataFields(String projectName);

}
