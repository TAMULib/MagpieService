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
	@Column(name="name")
	private String name;
	
	@Column(name="txt_uri")
	private String txtUri;
	
	@Column(name="pdf_uri")
	private String pdfUri;
		
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
	 * @param name
	 * @param status
	 */
	public DocumentImpl(String name, String status) {
		super();
		this.name = name;
		this.status = status;
	}
	
	/**
	 * 
	 * @param name
	 * @param uri
	 * @param status
	 */
	public DocumentImpl(String name, String txtUri, String pdfUri, String status) {
		super();
		this.name = name;
		this.txtUri = txtUri;
		this.pdfUri = pdfUri;
		this.status = status;
	}

	/**
	 * Gets filename.
	 * 
	 * @return		String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets filename.
	 * 
	 * @param 		filename			String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 */
	public String getTxtUri() {
		return txtUri;
	}

	/**
	 * 
	 */
	public void setTxtUri(String uri) {
		this.txtUri = uri;
	}
	
	/**
	 * 
	 */
	public String getPdfUri() {
		return pdfUri;
	}

	/**
	 * 
	 */
	public void setPdfUri(String uri) {
		this.pdfUri = uri;
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
