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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataField;
import edu.tamu.app.model.repo.custom.CustomDocumentRepo;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface DocumentRepo extends JpaRepository <Document, Long>, CustomDocumentRepo {
	
	public Document create(String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status, List<MetadataField> metadata);
	
	@Override
	public void delete(Document document);
	
	@Override
	public void deleteAll();
	
	/**
	 * Retrieve document by name.
	 * 
	 * @param 		name				String
	 * 
	 * @return		Document
	 * 
	 */
	public Document findByName(String name);
	
	/**
	 * Retrieve documents by status.
	 * 
	 * @param 		name				String
	 * 
	 * @return		Document
	 * 
	 */
	public List<Document> findByStatus(String status);
	
	/**
	 * Retrieve document by status and project.
	 * 
	 * @param 		name				String
	 * 
	 * @return		Document
	 * 
	 */
	public List<Document> findByStatusAndProject(String status, String project);
	
	/**
	 * Retrieve document by name.
	 * 
	 * @param 		page				Pageable
	 * @param 		name				String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByNameContainingIgnoreCase(Pageable page, String name);
	
	/**
	 * Retrieve document by name and status.
	 * 
	 * @param 		page				Pageable
	 * @param 		name1				String
	 * @param 		status1				String
	 * @param 		name2				String
	 * @param 		status2				String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatusContainingIgnoreCase(Pageable page, String name1, String status1, String name2, String status2);
	
	/**
	 * Retrieve document by name and status.
	 * 
	 * @param 		page				Pageable
	 * @param 		name				String
	 * @param 		status				String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCase(Pageable page, String name, String status);
	
	/**
	 * Retrieve document by name and annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		name				String
	 * @param 		annotator			String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByNameContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String name, String annotator);
	
	
	/**
	 * Retrieve document by status.
	 * 
	 * @param 		page				Pageable
	 * @param 		status1				String
	 * @param 		status2				String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByStatusContainingIgnoreCaseOrStatusContainingIgnoreCase(Pageable page, String status1, String status2);
	
	/**
	 * Retrieve document by status.
	 * 
	 * @param 		page				Pageable
	 * @param 		status				String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByStatusContainingIgnoreCase(Pageable page, String status);
	
	/**
	 * Retrieve document by status and annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		status1				String
	 * @param 		annotator1			String
	 * @param 		status2				String
	 * @param 		annotator2			String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String status1, String annotator1, String status2, String annotator2);
	
	/**
	 * Retrieve document by status and annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		status				String
	 * @param 		annotator			String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String status, String annotator);
	
	/**
	 * Retrieve document by annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		annotator			String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByAnnotatorContainingIgnoreCase(Pageable page, String annotator);
		
	/**
	 * Retrieve document by name and status.
	 * 
	 * @param 		page				Pageable
	 * @param 		name1				String
	 * @param 		status1				String
	 * @param 		annotator1			String
	 * @param 		name2				String
	 * @param 		status2				String
	 * @param 		annotator2			String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String name1, String status1, String annotator1, String name2, String status2, String annotator2);
	
	/**
	 * Retrieve document by name and status.
	 * 
	 * @param 		page				Pageable
	 * @param 		name				String
	 * @param 		status				String
	 * @param 		annotator			String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	public Page<Document> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, String name, String status, String annotator);
	
}
