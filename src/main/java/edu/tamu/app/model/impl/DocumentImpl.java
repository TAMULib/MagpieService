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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
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
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	@Column(name="txt_uri")
	private String txtUri;
	
	@Column(name="pdf_uri")
	private String pdfUri;
		
	private String status;
	
	private String annotator;
	
	private String notes;
	
	@OneToOne(cascade = CascadeType.PERSIST)
	private DocumentProfileImpl profile;
	
	/**
	 * 
	 */
	public DocumentImpl() {
		super();
		this.profile = new DocumentProfileImpl("DISSERTATION");
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
		this.profile = new DocumentProfileImpl("DISSERTATION");
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
		this.profile = new DocumentProfileImpl("DISSERTSTION");
	}

	/**
	 * Gets name.
	 * 
	 * @return		String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets name.
	 * 
	 * @param 		name				String
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
	
	/**
	 * Gets document profile.
	 * 
	 * @return		DocumentProfileImpl
	 */
	public DocumentProfileImpl getDocumentProfile() {
		return profile;
	}

	/**
	 * Sets documentProfile.
	 * 
	 * @param 		type			DocumentProfileImpl
	 */
	public void setDocumentProfile(DocumentProfileImpl profile) {
		this.profile = profile;
	}
	
}
