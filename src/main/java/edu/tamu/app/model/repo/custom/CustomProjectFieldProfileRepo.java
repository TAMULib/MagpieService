/* 
 * CustomProjectFieldProfileRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectFieldProfile;

/**
 * 
 * 
 * @author
 *
 */
public interface CustomProjectFieldProfileRepo {
	
	public ProjectFieldProfile create(MetadataFieldLabel label, Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);
	
	public void delete(ProjectFieldProfile field);
	
	public void deleteAll();
	
}
