package edu.tamu.app.model.repo.custom;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;

public interface DocumentRepoCustom {

    public Document create(Project project, String name, String documentPath, String status);

    public Page<Document> pageableDynamicDocumentQuery(Map<String, String[]> filters, Pageable pageable);

}
