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
	 * Retrieve document by filename.
	 * 
	 * @param 		filename			String
	 * 
	 * @return		DocumentImpl
	 * 
	 */
	public DocumentImpl findByFilename(String filename);
	
	/**
	 * Retrieve document by filename.
	 * 
	 * @param 		page				Pageable
	 * @param 		filename			String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByFilenameContainingIgnoreCase(Pageable page, String filename);
	
	/**
	 * Retrieve document by filename and status.
	 * 
	 * @param 		page				Pageable
	 * @param 		filename			String
	 * @param 		status				String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByFilenameContainingIgnoreCaseAndStatusContainingIgnoreCaseOrFilenameContainingIgnoreCaseAndStatusContainingIgnoreCase(Pageable page, String filename1, String status1, String filename2, String status2);
	
	/**
	 * Retrieve document by filename and annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		filename			String
	 * @param 		annotator			String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByFilenameContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String filename, String annotator);
	
	
	/**
	 * Retrieve document by status.
	 * 
	 * @param 		page				Pageable
	 * @param 		status				String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByStatusContainingIgnoreCaseOrStatusContainingIgnoreCase(Pageable page, String status1, String status2);
	
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
	public Page<DocumentImpl> findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String status1, String annotator1, String status2, String annotator2);
	
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
	 * Retrieve document by filename and status.
	 * 
	 * @param 		page				Pageable
	 * @param 		filename			String
	 * @param 		status				String
	 * @param 		annotator			String
	 * 
	 * @return		Page<DocumentImpl>
	 * 
	 */
	public Page<DocumentImpl> findByFilenameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrFilenameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String filename1, String status1, String annotator1, String filename2, String status2, String annotator2);
	
}
