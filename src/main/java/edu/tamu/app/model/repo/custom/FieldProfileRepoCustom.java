/* 
 * FieldProfileRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.Project;

/**
 * 
 * 
 * @author
 *
 */
public interface FieldProfileRepoCustom {

    public FieldProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);

}
