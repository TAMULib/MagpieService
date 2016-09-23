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

import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectProfile;

/**
 * 
 * 
 * @author
 *
 */
public interface ProjectProfileRepoCustom {

    public ProjectProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);

    public void delete(ProjectProfile profile);

    public void deleteAll();

}
