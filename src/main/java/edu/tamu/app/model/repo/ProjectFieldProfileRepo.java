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
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectFieldProfile;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface ProjectFieldProfileRepo extends JpaRepository <ProjectFieldProfile, Long> {
	
	public ProjectFieldProfile create(MetadataFieldLabel label, Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);
	
	public ProjectFieldProfile findByLabelAndProject(MetadataFieldLabel label, Project project);
	
	public List<ProjectFieldProfile> findByLabel(MetadataFieldLabel label);
	
	public List<ProjectFieldProfile> findByProject(Project project);
	
	@Override
	public void delete(ProjectFieldProfile field);
	
	@Override
	public void deleteAll();
	
}
