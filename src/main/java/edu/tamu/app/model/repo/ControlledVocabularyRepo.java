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
import edu.tamu.app.model.repo.custom.CustomControlledVocabularyRepo;

/**
 *
 * 
 * @author
 *
 */
@Repository
public interface ControlledVocabularyRepo extends JpaRepository<ControlledVocabulary, Long>, CustomControlledVocabularyRepo {

	public ControlledVocabulary create(String name);
	
	public void delete(ControlledVocabulary cv);
	
	public void deleteAll();
		
	public ControlledVocabulary findByValue(String value);
		
}
