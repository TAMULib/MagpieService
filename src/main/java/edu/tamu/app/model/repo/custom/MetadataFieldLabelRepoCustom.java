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
import edu.tamu.app.model.ProjectLabelProfile;

/**
 * 
 * 
 * @author
 *
 */
public interface MetadataFieldLabelRepoCustom {

	public MetadataFieldLabel create(String name, ProjectLabelProfile profile);
	
	public void delete(MetadataFieldLabel label);
	
	public void deleteAll();
	
}
