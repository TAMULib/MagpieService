/* 
 * ProjectFieldProfileRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.InputType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.custom.ProjectLabelProfileRepoCustom;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface ProjectLabelProfileRepo extends JpaRepository <ProjectLabelProfile, Long>, ProjectLabelProfileRepoCustom {
	
	public ProjectLabelProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);
	
	public ProjectLabelProfile findByProjectAndGlossAndRepeatableAndReadOnlyAndHiddenAndRequiredAndInputTypeAndDefaultValue(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);
	
	public List<ProjectLabelProfile> findByProject(Project project);
	
	@Override
	public void delete(ProjectLabelProfile profile);
	
	@Override
	public void deleteAll();
	
}
