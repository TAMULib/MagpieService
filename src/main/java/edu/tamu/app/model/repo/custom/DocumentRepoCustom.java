/* 
 * DocumentRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;

/**
 * 
 * 
 * @author
 *
 */
public interface DocumentRepoCustom {

	public Document create(Project project, String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status);

	public Page<Document> pageableDynamicDocumentQuery(Map<String, String[]> filters, Pageable pageable);

}
