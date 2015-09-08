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

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldRepo extends JpaRepository<MetadataField, Long> {

	public List<MetadataField> findByDocument(Document document);
	
	public List<MetadataField> findByLabel(MetadataFieldLabel label);
	
	public MetadataField findByDocumentAndLabel(Document document, MetadataFieldLabel label);

}
