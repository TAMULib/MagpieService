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
import edu.tamu.app.model.repo.custom.CustomMetadataFieldLabelRepo;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldLabelRepo extends JpaRepository <MetadataFieldLabel, Long>, CustomMetadataFieldLabelRepo {
	
	public MetadataFieldLabel create(String name);
	
	public MetadataFieldLabel findByName(String name);
	
	@Override
	public void delete(MetadataFieldLabel label);
	
	@Override
	public void deleteAll();

}
