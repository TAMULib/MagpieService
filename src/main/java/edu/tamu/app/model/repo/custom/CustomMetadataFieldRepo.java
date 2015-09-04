/* 
 * CustomMetadataFieldRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldLabel;

/**
 * 
 * 
 * @author
 *
 */
public interface CustomMetadataFieldRepo {

	public MetadataField create(Document document, MetadataFieldLabel label);
	
	public void delete(MetadataField value);
	
	public void deleteAll();
	
}
