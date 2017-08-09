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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
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

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Override
    public synchronized Document create(Project project, String name, String documentPath, String status) {
        Document document = documentRepo.findByProjectNameAndName(project.getName(), name);
        if (document == null) {
            document = documentRepo.save(new Document(project, name, documentPath, status));
        }
        return document;
    }

    @Override
    public Page<Document> pageableDynamicDocumentQuery(Map<String, String[]> filters, Pageable pageable) {
        return documentRepo.findAll(new DocumentSpecification<Document>(filters), pageable);
    }

    @Override
    public Document fullSave(Document document) {
        List<MetadataFieldGroup> mfgs = new ArrayList<MetadataFieldGroup>();
        for (MetadataFieldGroup mfg : document.getFields()) {
            List<MetadataFieldValue> mfvs = new ArrayList<MetadataFieldValue>();
            for (MetadataFieldValue mfv : mfg.getValues()) {
                mfv = metadataFieldValueRepo.save(mfv);
                mfvs.add(mfv);
            }
            mfg.setValues(mfvs);
            mfg = metadataFieldGroupRepo.save(mfg);
            mfgs.add(mfg);
        }
        document.setFields(mfgs);
        return documentRepo.save(document);
    }

}
