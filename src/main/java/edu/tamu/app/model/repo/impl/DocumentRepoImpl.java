/* 
 * DocumentRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.custom.DocumentRepoCustom;
import edu.tamu.app.model.repo.specification.DocumentSpecification;

/**
 *
 * 
 * @author
 *
 */
public class DocumentRepoImpl implements DocumentRepoCustom {

    @Autowired
    private DocumentRepo documentRepo;

    @Override
    public synchronized Document create(Project project, String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String documentPath, String status) {
        Document document = documentRepo.findByProjectNameAndName(project.getName(), name);
        if (document == null) {
            document = documentRepo.save(new Document(project, name, txtUri, pdfUri, txtPath, pdfPath, documentPath, status));
        }
        return document;
    }

    @Override
    public Page<Document> pageableDynamicDocumentQuery(Map<String, String[]> filters, Pageable pageable) {
        return documentRepo.findAll(new DocumentSpecification<Document>(filters), pageable);
    }

}
