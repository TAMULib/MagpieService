/* 
 * MetadataFieldLabelRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.ProjectLabelProfile;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.custom.MetadataFieldLabelRepoCustom;

/**
*
* 
* @author
*
*/
public class MetadataFieldLabelRepoImpl implements MetadataFieldLabelRepoCustom {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldGroupRepo metadataFieldRepo;
	
	@Override
	public synchronized MetadataFieldLabel create(String name, ProjectLabelProfile profile) {		
		MetadataFieldLabel label = metadataFieldLabelRepo.findByName(name);
		if(label == null) {
			return metadataFieldLabelRepo.save(new MetadataFieldLabel(name, profile));
		}		
		return label;
	}

	@Override
	@Transactional
	public void delete(MetadataFieldLabel label) {
		ProjectLabelProfile profile = label.getProfile();
		if(profile != null) {
			label.setProfile(null);
			metadataFieldLabelRepo.save(label);
		}
		
		Set<MetadataFieldGroup> fields = label.getFields();		 
		if(fields.size() > 0) {
			fields.parallelStream().forEach(field -> {
				field.setLabel(null);
				metadataFieldRepo.save(field);
			});
			label.clearFields();
		}
		 
		entityManager.remove(entityManager.contains(label) ? label : entityManager.merge(label));
	}
	
	@Override
	public void deleteAll() {
		metadataFieldLabelRepo.findAll().parallelStream().forEach(label -> {
			metadataFieldLabelRepo.delete(label);
		});
	}

}
