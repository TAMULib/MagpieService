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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.impl.DocumentImpl;

/**
 * Document repository.
 * 
 * @author
 *
 */
@Repository
public interface DocumentRepo extends JpaRepository <DocumentImpl, Long>{
	
	/**
	 * Retrieve document by filename.
	 * 
	 * @param 		filename			String
	 * 
	 * @return		DocumentImpl
	 * 
	 */
	public DocumentImpl getDocumentByFilename(String filename);

}
