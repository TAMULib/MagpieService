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

import java.util.List;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.CustomDocumentRepo;

/**
*
* 
* @author
*
*/
public class DocumentRepoImpl implements CustomDocumentRepo {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;

	@Override
	public Document create(String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status) {
		Document document = documentRepo.findByName(name);	
		if(document == null) {
			return documentRepo.save(new Document(name, txtUri, pdfUri, txtPath, pdfPath, status));
		}		
		return document;
	}
	
	@Override
	public Document create(String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status, List<MetadataFieldLabel> labels) {
		Document document = documentRepo.findByName(name);	
		if(document == null) {
			document = documentRepo.save(new Document(name, txtUri, pdfUri, txtPath, pdfPath, status));
			for(MetadataFieldLabel label : labels) {
				document.addMetadataField(metadataFieldRepo.create(document, label));
			}
			return documentRepo.save(document);
		}		
		return document;
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
		
		List<MetadataField> fields = document.getMetadataFields();
		if(fields.size() > 0) {
			fields.forEach(field -> {
				field.setDocument(null);
				metadataFieldRepo.save(field);
			});
			document.clearMetadataFields();
		}
		
		entityManager.remove(entityManager.contains(document) ? document : entityManager.merge(document));
	}
	
	@Override
	public void deleteAll() {
		documentRepo.findAll().forEach(document -> {
			documentRepo.delete(document);
		});
	}	

}
