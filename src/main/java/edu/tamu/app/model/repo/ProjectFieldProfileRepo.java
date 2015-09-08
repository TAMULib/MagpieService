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
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.custom.CustomProjectFieldProfileRepo;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface ProjectFieldProfileRepo extends JpaRepository <ProjectLabelProfile, Long>, CustomProjectFieldProfileRepo {
	
	public ProjectLabelProfile create(MetadataFieldLabel label, Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);
	
	public ProjectLabelProfile findByLabelAndProject(MetadataFieldLabel label, Project project);
	
	public List<ProjectLabelProfile> findByLabel(MetadataFieldLabel label);
	
	public List<ProjectLabelProfile> findByProject(Project project);
	
	@Override
	public void delete(ProjectLabelProfile field);
	
	@Override
	public void deleteAll();
	
}
