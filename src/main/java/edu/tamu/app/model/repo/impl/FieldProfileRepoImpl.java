/* 
 * ProjectFieldProfileRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.custom.FieldProfileRepoCustom;

/**
 *
 * 
 * @author
 *
 */
public class FieldProfileRepoImpl implements FieldProfileRepoCustom {

	@Autowired
	private FieldProfileRepo fieldProfileRepo;

	@Override
	public synchronized FieldProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue) {
		FieldProfile fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, gloss);
		if (fieldProfile == null) {
			fieldProfile = fieldProfileRepo.save(new FieldProfile(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue));
		}
		return fieldProfile;
	}

}
