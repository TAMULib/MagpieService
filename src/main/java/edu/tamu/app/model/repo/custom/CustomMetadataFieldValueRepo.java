/* 
 * CustomMetadataFieldValueRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldValue;

/**
 * 
 * 
 * @author
 *
 */
public interface CustomMetadataFieldValueRepo {

	public MetadataFieldValue create(ControlledVocabulary cv, MetadataField field);
	
	public MetadataFieldValue create(String value, MetadataField field);
	
	public void delete(MetadataFieldValue value);
	
	public void deleteAll();
	
}
