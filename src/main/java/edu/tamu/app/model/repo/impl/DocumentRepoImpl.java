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

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.DocumentRepoCustom;

/**
*
* 
* @author
*
*/
public class DocumentRepoImpl implements DocumentRepoCustom {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private MetadataFieldGroupRepo metadataFieldRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;

	@Override
	public Document create(Project project, String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status) {
		Document document = documentRepo.findByName(name);	
		if(document == null) {
			return documentRepo.save(new Document(project, name, txtUri, pdfUri, txtPath, pdfPath, status));
		}		
		return document;
	}
	
	@Override
	public Document update(Document newDocument) {
		Document oldDocument = documentRepo.findByName(newDocument.getName());
		
		newDocument.getFields().parallelStream().forEach(field -> {
			MetadataFieldGroup oldField = metadataFieldRepo.findByDocumentAndLabel(oldDocument, field.getLabel());
			oldField.setValues(field.getValues());
			field.getValues().parallelStream().forEach(value -> {
				value.setField(oldField);
				metadataFieldValueRepo.save(value);
			});
		});
		
		return documentRepo.save(oldDocument);
	}
	
	@Override
	@Transactional
	public void delete(Document document) {				
		Project project = document.getProject();
		if(project != null) {
			document.setProject(null);
			project.removeDocument(document);
			projectRepo.save(project);
		}
		
		Set<MetadataFieldGroup> fields = document.getFields();
		if(fields.size() > 0) {
			fields.parallelStream().forEach(field -> {
				field.setDocument(null);
				metadataFieldRepo.save(field);
			});
			document.clearFields();
		}
		
		entityManager.remove(entityManager.contains(document) ? document : entityManager.merge(document));
	}
	
	@Override
	public void deleteAll() {
		documentRepo.findAll().parallelStream().forEach(document -> {
			documentRepo.delete(document);
		});
	}	

}
