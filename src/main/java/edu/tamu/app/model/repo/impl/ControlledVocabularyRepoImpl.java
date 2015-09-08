/* 
 * ControlledVocabularyRepoImpl.java 
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
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.ControlledVocabularyRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.custom.CustomControlledVocabularyRepo;

/**
*
* 
* @author
*
*/
public class ControlledVocabularyRepoImpl implements CustomControlledVocabularyRepo {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ControlledVocabularyRepo controlledVocabularyRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Override
	public synchronized ControlledVocabulary create(String value) {		
		ControlledVocabulary cv = controlledVocabularyRepo.findByValue(value);		
		if(cv == null) {
			return controlledVocabularyRepo.save(new ControlledVocabulary(value));
		}		
		return cv;
	}
	
	@Override
	@Transactional
	public void delete(ControlledVocabulary cv) {
		 List<MetadataFieldValue> values = metadataFieldValueRepo.findByCv(cv); 
		 if(values.size() > 0) {	
			 values.forEach(value -> {
				 value.setCv(null);
				 metadataFieldValueRepo.save(value);
			 });
			 cv.clearValues();
		 }
		 
		 entityManager.remove(entityManager.contains(cv) ? cv : entityManager.merge(cv));
	}
	
	@Override
	public void deleteAll() {
		controlledVocabularyRepo.findAll().forEach(cv -> {
			controlledVocabularyRepo.delete(cv);
		});
	}

}
