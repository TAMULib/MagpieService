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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectLabelProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.ProjectLabelProfileRepoCustom;

/**
*
* 
* @author
*
*/
public class ProjectLabelProfileRepoImpl implements ProjectLabelProfileRepoCustom {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ProjectLabelProfileRepo projectFieldProfileRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private ProjectRepo projectRepo;
		
	@Override
	public ProjectLabelProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue) {
		ProjectLabelProfile profile = projectFieldProfileRepo.findByProjectAndGlossAndRepeatableAndReadOnlyAndHiddenAndRequiredAndInputTypeAndDefaultValue(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue);
		if(profile == null) {
			return projectFieldProfileRepo.save(new ProjectLabelProfile(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue));
		}
		return profile;
	}
	
	@Override
	@Transactional
	public void delete(ProjectLabelProfile profile) {
		List<MetadataFieldLabel> labels = profile.getLabels();
		if(labels.size() > 0) {
			labels.forEach(l -> {
				l.setProfile(null);
				metadataFieldLabelRepo.save(l);
			});
			profile.clearLabels();
		}
		
		Project project = profile.getProject();
		if(project != null) {
			profile.setProject(null);
			project.removeProfile(profile);
			projectRepo.save(project);			
		}
				 
		entityManager.remove(entityManager.contains(profile) ? profile : entityManager.merge(profile));
	}
	
	@Override
	public void deleteAll() {
		projectFieldProfileRepo.findAll().forEach(profile -> {
			projectFieldProfileRepo.delete(profile);
		});
	}
	
}
