/* 
 * DocumentRepo.java 
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

import edu.tamu.app.model.impl.MetadataFieldImpl;

/**
 * Document repository.
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldRepo extends JpaRepository <MetadataFieldImpl, Long>{
	
	/**
	 * Retrieve metadata by filename.
	 * 
	 * @param 		filename			String
	 * 
	 * @return		MetadataFieldImpl
	 * 
	 */
	public List<MetadataFieldImpl> getMetadataFieldsByFilename(String filename);
	
	/**
	 * Retrieve metadata by status.
	 * 
	 * @param 		status			String
	 * 
	 * @return		MetadataFieldImpl
	 * 
	 */
	public List<MetadataFieldImpl> getMetadataFieldsByStatus(String status);

}
