/* 
 * CustomControlledVocabularyRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.ControlledVocabulary;

/**
*
* 
* @author
*
*/
public interface CustomControlledVocabularyRepo {

	public ControlledVocabulary create(String name);
	
	public void delete(ControlledVocabulary cv);
	
	public void deleteAll();
	
}
