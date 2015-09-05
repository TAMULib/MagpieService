/* 
 * MetadataFieldRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.custom.CustomMetadataFieldRepo;

/**
*
* 
* @author
*
*/
public class MetadataFieldRepoImpl implements CustomMetadataFieldRepo {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Override
	public MetadataField create(Document document, MetadataFieldLabel label) {
		MetadataField field = metadataFieldRepo.findByDocumentAndLabel(document, label);		
		if(field == null) {
			return metadataFieldRepo.save(new MetadataField(document, label));
		}
		return field;
	}
		
	@Override
	@Transactional
	public void delete(MetadataField field) {
		Document document = field.getDocument();
		if(document != null) {
			field.setDocument(null);
			document.removeMetadataField(field);
			documentRepo.save(document);			
		}
		
		MetadataFieldLabel label = field.getLabel();
		if(label != null) {
			field.setLabel(null);
			label.removeField(field);
			metadataFieldLabelRepo.save(label);			
		}
		
		List<MetadataFieldValue> values = field.getValues();
		if(values.size() > 0) {			
			values.forEach(value -> {
				ControlledVocabulary cv = value.getCv();
				if(cv != null) {
					cv.removeValue(value);
				}
				value.setField(null);
				metadataFieldValueRepo.save(value);
			});			
			field.clearValues();
		}

		entityManager.remove(entityManager.contains(field) ? field : entityManager.merge(field));
	}
	
	@Override
	public void deleteAll() {
		metadataFieldRepo.findAll().forEach(field -> {
			metadataFieldRepo.delete(field);
		});
	}
}
