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

import java.io.File;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.tamu.app.model.Document;

/**
 * Implementation of document object.
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
	
	private String project;
	
	@Column(name="txt_uri")
	private String txtUri;
	
	@Column(name="pdf_uri")
	private String pdfUri;
	
	private String pdfPath;
	
	private String txtPath;
		
	private String status;
	
	private String annotator;
	
	private String notes;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private List<MetadataLabelImpl> metadataLabels;
	
	/**
	 * Default constructor.
	 * 
	 */
	public DocumentImpl() {
		super();	
	}
	
	/**
	 * Constructor.
	 * 
	 * @param 		name			String
	 * @param 		uri				String
	 * @param 		status			String
	 * 
	 */
	public DocumentImpl(String name, String project, String txtUri, String pdfUri, String txtPath, String pdfPath, String status, List<MetadataLabelImpl> metadataLabels) {
		super();
		this.name = name;
		this.project = project;
		this.txtUri = txtUri;
		this.pdfUri = pdfUri;
		this.pdfPath = pdfPath;
		this.txtPath = txtPath;
		this.status = status;
		this.metadataLabels = metadataLabels;
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
	 * @param 		name			String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets project.
	 * 
	 * @return		String
	 */
	public String getProject() {
		return project;
	}

	/**
	 * Sets project.
	 * 
	 * @param 		name			String
	 */
	public void setProject(String project) {
		this.project = project;
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
	
	public String getPdfPath() {
		return pdfPath;
	}

	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}

	public String getTxtPath() {
		return txtPath;
	}

	public void setTxtPath(String txtPath) {
		this.txtPath = txtPath;
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
	 * @param 		status			String
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
	 * @param 		annotator		String
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
	 * @param 		annotator		String
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Gets id.
	 * 
	 * @return		Long
	 * 
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets id.
	 * 
	 * @param		id				Long
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets metadata labels.
	 * 
	 * @return 		List<MetadataLabelImpl>
	 * 
	 */
	public List<MetadataLabelImpl> getMetadataLabels() {
		return metadataLabels;
	}

	/**
	 * Sets metadata labels.
	 * 
	 * @param		metadataLabels	List<MetadataLabelImpl>
	 */
	public void setMetadataLabels(List<MetadataLabelImpl> metadataLabels) {
		this.metadataLabels = metadataLabels;
	}
	
	/**
	 *  Gets the file off the disk
	 *  
	 */
	public File pdf() {		
		return new File("src/main/resources/static/"+getPdfPath());
		
	}
	
	/**
	 *  Gets the file off the disk
	 *  
	 */
	public File txt() {		
		return new File("src/main/resources/static/"+getTxtPath());
		
	}
	
}
