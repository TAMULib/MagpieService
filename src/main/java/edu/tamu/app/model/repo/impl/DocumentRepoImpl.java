package edu.tamu.app.model.repo.impl;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.custom.DocumentRepoCustom;
import edu.tamu.app.model.repo.specification.DocumentSpecification;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class DocumentRepoImpl extends AbstractWeaverRepoImpl<Document, DocumentRepo> implements DocumentRepoCustom {

    @Autowired
    private DocumentRepo documentRepo;

    @Override
    public synchronized Document create(Project project, String name, String path, String status) {
        Document document = documentRepo.findByProjectNameAndName(project.getName(), name);
        if (document == null) {
            document = documentRepo.create(new Document(project, name, path.replace(ASSETS_PATH, ""), status));
        }
        return document;
    }

    @Override
    public Page<Document> pageableDynamicDocumentQuery(Map<String, String[]> filters, Pageable pageable) {
        return documentRepo.findAll(new DocumentSpecification<Document>(filters), pageable);
    }

    @Override
    protected String getChannel() {
        return "/channel/document";
    }

}
