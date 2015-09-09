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
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;

/**
 * 
 * 
 * @author
 *
 */
public interface ProjectLabelProfileRepoCustom {
	
	public ProjectLabelProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);
	
	public void delete(ProjectLabelProfile profile);
	
	public void deleteAll();
	
}
