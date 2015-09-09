/* 
 * ControlledVocabularyRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.repo.custom.ControlledVocabularyRepoCustom;

/**
 *
 * 
 * @author
 *
 */
@Repository
public interface ControlledVocabularyRepo extends JpaRepository <ControlledVocabulary, Long>, ControlledVocabularyRepoCustom {
	
	public ControlledVocabulary create(String name);
	
	public ControlledVocabulary findByValue(String value);
	
	@Override
	public void delete(ControlledVocabulary cv);
	
	@Override
	public void deleteAll();
	
}
