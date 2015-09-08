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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectFieldProfile;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectFieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.CustomProjectFieldProfileRepo;

/**
*
* 
* @author
*
*/
public class ProjectFieldProfileRepoImpl implements CustomProjectFieldProfileRepo {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private ProjectFieldProfileRepo projectFieldProfileRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private ProjectRepo projectRepo;
		
	@Override
	public ProjectFieldProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue) {
		ProjectFieldProfile profile = projectFieldProfileRepo.findByProject(project);		
		if(profile == null) {
			return projectFieldProfileRepo.save(new ProjectFieldProfile(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue));
		}
		return profile;
	}
	
	@Override
	@Transactional
	public void delete(ProjectFieldProfile profile) {
		MetadataFieldLabel label = profile.getLabel();
		if(label != null) {
			profile.setLabel(null);
			label.removeProfile(profile);
			metadataFieldLabelRepo.save(label);
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
