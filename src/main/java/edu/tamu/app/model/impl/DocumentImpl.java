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
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
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
