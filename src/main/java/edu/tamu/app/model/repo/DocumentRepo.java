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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.custom.DocumentRepoCustom;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface DocumentRepo extends JpaRepository<Document, Long>, DocumentRepoCustom {
	
	public Document create(Project project, String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status);
	
	public Document update(Document document);
	
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
	 * Retrieve all documents.
	 * 
	 * @param 		name				String
	 * 
	 * @return		Document
	 * 
	 */
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d")
	public Page<Object> findAllAsObjects(Pageable page);
	
	/**
	 * Retrieve document by name.
	 * 
	 * @param 		page				Pageable
	 * @param 		name				String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))")
	public Page<Object> findByNameContainingIgnoreCase(Pageable page, @Param("name") String name);
	
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
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE (LOWER(d.name) LIKE LOWER(CONCAT('%', :name1, '%')) AND LOWER(d.status) LIKE(CONCAT('%', :status1, '%'))) OR (LOWER(d.name) LIKE LOWER(CONCAT('%', :name2, '%')) AND LOWER(d.status) LIKE(CONCAT('%', :status2, '%')))")
	public Page<Object> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatusContainingIgnoreCase(Pageable page, @Param("name1") String name1, @Param("status1") String status1, @Param("name2") String name2, @Param("status2") String status2);
	
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
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.status) LIKE LOWER(CONCAT('%', :status, '%'))")
	public Page<Object> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCase(Pageable page, @Param("name") String name, @Param("status") String status);
	
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
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.annotator) LIKE LOWER(CONCAT('%', :annotator, '%'))")
	public Page<Object> findByNameContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, @Param("name") String name, @Param("annotator") String annotator);
	
	
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
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE LOWER(d.status) LIKE LOWER(CONCAT('%', :status1, '%')) OR LOWER(d.status) LIKE LOWER(CONCAT('%', :status2, '%'))")
	public Page<Object> findByStatusContainingIgnoreCaseOrStatusContainingIgnoreCase(Pageable page, @Param("status1") String status1, @Param("status2") String status2);
	
	/**
	 * Retrieve document by status.
	 * 
	 * @param 		page				Pageable
	 * @param 		status				String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE LOWER(d.status) LIKE LOWER(CONCAT('%', :status, '%'))")
	public Page<Object> findByStatusContainingIgnoreCase(Pageable page, @Param("status") String status);
	
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
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE (LOWER(d.status) LIKE LOWER(CONCAT('%', :status1, '%')) AND LOWER(d.annotator) LIKE(CONCAT('%', :annotator1, '%'))) OR (LOWER(d.status) LIKE LOWER(CONCAT('%', :status2, '%')) AND LOWER(d.annotator) LIKE(CONCAT('%', :annotator2, '%')))")
	public Page<Object> findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, @Param("status1") String status1, @Param("annotator1") String annotator1, @Param("status2") String status2, @Param("annotator2") String annotator2);
	
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
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE LOWER(d.status) LIKE LOWER(CONCAT('%', :status, '%')) AND LOWER(d.annotator) LIKE LOWER(CONCAT('%', :annotator, '%'))")
	public Page<Object> findByStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, @Param("status") String status, @Param("annotator") String annotator);
	
	/**
	 * Retrieve document by annotator.
	 * 
	 * @param 		page				Pageable
	 * @param 		annotator			String
	 * 
	 * @return		Page<Document>
	 * 
	 */
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE LOWER(d.annotator) LIKE LOWER(CONCAT('%', :annotator, '%'))")
	public Page<Object> findByAnnotatorContainingIgnoreCase(Pageable page, @Param("annotator") String annotator);
		
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
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE (LOWER(d.name) LIKE LOWER(CONCAT('%', :name1, '%')) AND LOWER(d.status) LIKE LOWER(CONCAT('%', :status1, '%')) AND LOWER(d.annotator) LIKE LOWER(CONCAT('%', :annotator1, '%'))) OR (LOWER(d.name) LIKE LOWER(CONCAT('%', :name2, '%')) AND LOWER(d.status) LIKE LOWER(CONCAT('%', :status2, '%')) AND LOWER(d.annotator) LIKE LOWER(CONCAT('%', :annotator2, '%')))")
	public Page<Object> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCaseOrNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, @Param("name1") String name1, @Param("status1") String status1, @Param("annotator1") String annotator1, @Param("name2") String name2, @Param("status2") String status2, @Param("annotator2") String annotator2);
	
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
	@Query(value = "SELECT d.name, d.status, d.annotator FROM Document d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(d.status) LIKE LOWER(CONCAT('%', :status, '%')) AND LOWER(d.annotator) LIKE LOWER(CONCAT('%', :annotator, '%'))")
	public Page<Object> findByNameContainingIgnoreCaseAndStatusContainingIgnoreCaseAndAnnotatorContainingIgnoreCase(Pageable page, @Param("name") String name, @Param("status") String status, @Param("annotator") String annotator);
	
}
