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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public interface DocumentRepo extends JpaRepository <DocumentImpl, Long> {
	
	/**
	 * Retrieve document by name.
	 * 
	 * @param 		name			String
	 * 
	 * @return		DocumentImpl
	 * 
	 */
	public DocumentImpl findByName(String name);
	
	/**
	 * Retrieve document by name.
	 * 
	 * @param 		page				Pageable
	 * @param 		name			String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByNameContainingIgnoreCase(Pageable page, String name);
	
	/**
	 * Retrieve document by name and status.
	 * 
	 * @param 		page				Pageable
	 * @param 		name			String
	 * @param 		status				String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCase(Pageable page, String name, String status);
	
	/**
	 * Retrieve document by name and annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		name			String
	 * @param 		annotator			String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByNameContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String name, String annotator);
	
	
	/**
	 * Retrieve document by status.
	 * 
	 * @param 		page				Pageable
	 * @param 		status				String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByStatusContainingIgnoreCase(Pageable page, String status);
	
	/**
	 * Retrieve document by status and annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		status				String
	 * @param 		annotator			String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String status, String annotator);
	
	
	/**
	 * Retrieve document by annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		annotator			String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByAnnotatorContainingIgnoreCase(Pageable page, String annotator);
		
	/**
	 * Retrieve document by name and status.
	 * 
	 * @param 		page				Pageable
	 * @param 		name			String
	 * @param 		status				String
	 * @param 		annotator			String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String name, String status, String annotator);
	
}
