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

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.custom.MetadataFieldLabelRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldLabelRepo extends WeaverRepo<MetadataFieldLabel>, MetadataFieldLabelRepoCustom {

    public MetadataFieldLabel findByNameAndProfile(String name, FieldProfile profile);

}
