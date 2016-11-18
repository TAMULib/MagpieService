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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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

    public Page<Document> findAll(Specification<Document> specification, Pageable pageable);

    public Page<Document> findAll(Pageable pageable);

    public Document findByProjectNameAndName(String projectName, String name);

    public List<Document> findByStatus(String status);

    public List<Document> findByProjectNameAndStatus(String projectName, String status);

}
