/* 
 * CustomDocumentRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import java.util.List;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataField;

/**
 * 
 * 
 * @author
 *
 */
public interface CustomDocumentRepo {

	public Document create(String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status, List<MetadataField> metadata);
	
	public void delete(Document document);
	
	public void deleteAll();
	
}
