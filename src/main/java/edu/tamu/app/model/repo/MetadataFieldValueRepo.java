/* 
 * MetadataFieldValueRepo.java 
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

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.custom.MetadataFieldValueRepoCustom;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldValueRepo extends JpaRepository<MetadataFieldValue, Long>, MetadataFieldValueRepoCustom {

    public MetadataFieldValue findByValueAndField(String value, MetadataFieldGroup field);

    public MetadataFieldValue findByCvAndField(ControlledVocabulary cv, MetadataFieldGroup field);

    public MetadataFieldValue findByValueAndCvAndField(String value, ControlledVocabulary cv, MetadataFieldGroup field);

}
