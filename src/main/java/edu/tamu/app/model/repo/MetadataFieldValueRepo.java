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
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldValue;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldValueRepo extends JpaRepository <MetadataFieldValue, Long> {
	
	public MetadataFieldValue create(ControlledVocabulary cv, MetadataField field);
	
	public MetadataFieldValue create(String value, MetadataField field);

	public MetadataFieldValue findByValueAndField(String value, MetadataField field);
	
	public MetadataFieldValue findByCvAndField(ControlledVocabulary cv, MetadataField field);
	
	public List<MetadataFieldValue> findByValue(String value);
	
	public List<MetadataFieldValue> findByField(MetadataField field);
	
	public List<MetadataFieldValue> findByCv(ControlledVocabulary cv);
	
	@Override
	public void delete(MetadataFieldValue value);
	
	@Override
	public void deleteAll();
	
}
