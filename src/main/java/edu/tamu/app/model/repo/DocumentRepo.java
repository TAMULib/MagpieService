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

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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
public interface DocumentRepo extends JpaRepository<Document, Long>, DocumentRepoCustom, JpaSpecificationExecutor<Document> {

    public Document create(Project project, String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status);

    public Document update(Document document);

    @Override
    public void delete(Document document);

    @Override
    public void deleteAll();

    public Page<Document> findAll(Specification<Document> specification, Pageable pageable);

    public Page<Document> findAll(Pageable pageable);

    public Document findByName(String name);

    public List<Document> findByStatus(String status);

    public List<Document> findByProjectNameAndStatus(String projectName, String status);

    public List<Document> findByProjectNameAndStatusNot(String projectName, String status);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Document set annotator = :annotator, status = :status, notes = :notes WHERE name = :name")
    public int quickSave(@Param("name") String name, @Param("annotator") String annotator, @Param("status") String status, @Param("notes") String notes);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Document set status = :status WHERE name = :name")
    public int quickUpdateStatus(@Param("name") String name, @Param("status") String status);

}
