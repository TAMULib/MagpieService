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
import edu.tamu.app.model.ProjectFieldProfile;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldLabelRepo extends JpaRepository<MetadataFieldLabel, Long> {

	public MetadataFieldLabel findByNameAndProfile(String name, ProjectFieldProfile profile);

}
