/* 
 * CustomMetadataFieldLabelRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.MetadataFieldLabel;

/**
 * 
 * 
 * @author
 *
 */
public interface CustomMetadataFieldLabelRepo {

	public MetadataFieldLabel create(String name);
	
	public void delete(MetadataFieldLabel label);
	
	public void deleteAll();
	
}
