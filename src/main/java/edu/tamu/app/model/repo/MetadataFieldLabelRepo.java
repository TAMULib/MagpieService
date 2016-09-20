/* 
 * MetadataFieldLabelRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.ProjectProfile;
import edu.tamu.app.model.repo.custom.MetadataFieldLabelRepoCustom;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldLabelRepo extends JpaRepository <MetadataFieldLabel, Long>, MetadataFieldLabelRepoCustom {
	
	public MetadataFieldLabel create(String name, ProjectProfile profile);
	
	public MetadataFieldLabel findByName(String name);
	
	@Override
	public void delete(MetadataFieldLabel label);
	
	@Override
	public void deleteAll();

}
