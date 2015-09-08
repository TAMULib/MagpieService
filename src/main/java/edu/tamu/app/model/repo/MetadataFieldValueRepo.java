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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.custom.CustomMetadataFieldValueRepo;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldValueRepo extends JpaRepository <MetadataFieldValue, Long>, CustomMetadataFieldValueRepo {
	
	public MetadataFieldValue create(ControlledVocabulary cv, MetadataFieldGroup field);
	
	public MetadataFieldValue create(String value, MetadataFieldGroup field);

	public MetadataFieldValue findByValueAndField(String value, MetadataFieldGroup field);
	
	public MetadataFieldValue findByCvAndField(ControlledVocabulary cv, MetadataFieldGroup field);
	
	public List<MetadataFieldValue> findByValue(String value);
	
	public List<MetadataFieldValue> findByField(MetadataFieldGroup field);
	
	public List<MetadataFieldValue> findByCv(ControlledVocabulary cv);
	
	@Override
	public void delete(MetadataFieldValue value);
	
	@Override
	public void deleteAll();
	
}
