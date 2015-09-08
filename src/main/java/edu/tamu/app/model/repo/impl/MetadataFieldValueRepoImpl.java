/* 
 * MetadataFieldValueRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.custom.CustomMetadataFieldValueRepo;

/**
*
* 
* @author
*
*/
public class MetadataFieldValueRepoImpl implements CustomMetadataFieldValueRepo {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;
	
	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
	
	@Override
	public MetadataFieldValue create(ControlledVocabulary cv, MetadataFieldGroup field) {
		MetadataFieldValue value = metadataFieldValueRepo.findByCvAndField(cv, field);		
		if(value == null) {
			return metadataFieldValueRepo.save(new MetadataFieldValue(cv, field));
		}
		return value;
	}
	
	@Override
	public MetadataFieldValue create(String value, MetadataFieldGroup field) {		
		MetadataFieldValue metadataFieldValue = metadataFieldValueRepo.findByValueAndField(value, field);		
		if(metadataFieldValue == null) {
			metadataFieldValue = metadataFieldValueRepo.save(new MetadataFieldValue(value, field));
		}
		field.addValue(metadataFieldValue);
		metadataFieldRepo.save(field);
		return metadataFieldValue;
	}

	@Override
	@Transactional
	public void delete(MetadataFieldValue value) {
		MetadataFieldGroup field = value.getField();
		if(field != null) {
			value.setField(null);
			field.removeValue(value);
			metadataFieldRepo.save(field);			
		}
		
		ControlledVocabulary cv = value.getCv();
		if(cv != null) {
			value.setCv(null);
			cv.removeValue(value);
			controlledVocabularyRepo.save(cv);			
		}
		
		entityManager.remove(entityManager.contains(value) ? value : entityManager.merge(value));
	}
	
	@Override
	public void deleteAll() {
		metadataFieldValueRepo.findAll().forEach(value -> {
			metadataFieldValueRepo.delete(value);
		});
	}

}
