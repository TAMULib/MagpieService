/* 
 * DocumentImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.tamu.app.model.Document;

/**
 * 
 * @author 
 *
 */
@Entity
@Table(name="all_documents")
public class DocumentImpl implements Document {
	
	@Id
	@Column(name="filename")
	private String filename;
		
	@Column(name="status")
	private String status;
	
	@Column(name="annotator")
	private String annotator;
	
	@Column(name="notes")
	private String notes;
	
	/**
	 * 
	 */
	public DocumentImpl() {
		super();
	}
	
	/**
	 * 
	 * @param filename
	 * @param status
	 */
	public DocumentImpl(String filename, String status) {
		super();
		this.filename = filename;
		this.status = status;
	}

	/**
	 * Gets filename.
	 * 
	 * @return		String
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets filename.
	 * 
	 * @param 		filename			String
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Gets status.
	 * 
	 * @return		String
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets status.
	 * 
	 * @param 		status				String
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * Gets annotator.
	 * 
	 * @return		String
	 */
	public String getAnnotator() {
		return annotator;
	}

	/**
	 * Sets annotator.
	 * 
	 * @param 		annotator			String
	 */
	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}
	
	/**
	 * Gets notes.
	 * 
	 * @return		String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets notes.
	 * 
	 * @param 		annotator			String
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
