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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.ProjectFieldProfile;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectFieldProfileRepo;
import edu.tamu.app.model.repo.custom.CustomMetadataFieldLabelRepo;

/**
*
* 
* @author
*
*/
public class MetadataFieldLabelRepoImpl implements CustomMetadataFieldLabelRepo {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private ProjectFieldProfileRepo projectFieldProfileRepo;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;
	
	@Override
	public synchronized MetadataFieldLabel create(String name) {		
		MetadataFieldLabel label = metadataFieldLabelRepo.findByName(name);
		if(label == null) {
			return metadataFieldLabelRepo.save(new MetadataFieldLabel(name));
		}		
		return label;
	}

	@Override
	@Transactional
	public void delete(MetadataFieldLabel label) {

		List<ProjectFieldProfile> profiles = label.getProfiles();		 
		if(profiles.size() > 0) {
			profiles.forEach(profile -> {
				profile.setLabel(null);
				projectFieldProfileRepo.save(profile);
			});
			label.clearProfiles();
		}
		 
		List<MetadataField> fields = label.getFields();		 
		if(fields.size() > 0) {
			fields.forEach(field -> {
				field.setLabel(null);
				metadataFieldRepo.save(field);
			});
			label.clearFields();
		}
		 		 
		entityManager.remove(entityManager.contains(label) ? label : entityManager.merge(label));
	}
	
	@Override
	public void deleteAll() {
		metadataFieldLabelRepo.findAll().forEach(label -> {
			metadataFieldLabelRepo.delete(label);
		});
	}

}
