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
	 * 
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * 
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * 
	 */
	public String getAnnotator() {
		return annotator;
	}

	/**
	 * 
	 */
	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}
	
}
