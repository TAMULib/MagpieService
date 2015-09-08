/* 
 * MetadataFieldRepo.java 
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

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.custom.CustomMetadataFieldRepo;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldRepo extends JpaRepository <MetadataField, Long>, CustomMetadataFieldRepo {
			
	public MetadataField create(Document document, MetadataFieldLabel label);
	
	public List<MetadataField> findByDocument(Document document);
	
	public List<MetadataField> findByLabel(MetadataFieldLabel label);
	
	public MetadataField findByDocumentAndLabel(Document document, MetadataFieldLabel label);
	
	@Override
	public void delete(MetadataField field);
	
	@Override
	public void deleteAll();
	
}
