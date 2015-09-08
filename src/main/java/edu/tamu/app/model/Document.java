/* 
 * Document.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * 
 * @author 
 *
 */
@Entity
@Table
public class Document {
	
	@Id
	private String name;
	
	private String status;
	
	private String annotator;
	
	private String notes;

	private String txtUri;

	private String pdfUri;
	
	private String pdfPath;
	
	private String txtPath;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Project.class, property = "name") 
	@JsonIdentityReference(alwaysAsId = true)
	private Project project;
	
	@OneToMany(mappedBy="document", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<MetadataFieldGroup> fields = new ArrayList<MetadataFieldGroup>();
	
	public Document() { }
	
	public Document(Project project, String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status) {
		this.project = project;
		this.name = name;
		this.txtUri = txtUri;
		this.pdfUri = pdfUri;
		this.pdfPath = pdfPath;
		this.txtPath = txtPath;
		this.status = status;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAnnotator() {
		return annotator;
	}

	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getTxtUri() {
		return txtUri;
	}

	public void setTxtUri(String txtUri) {
		this.txtUri = txtUri;
	}

	public String getPdfUri() {
		return pdfUri;
	}

	public void setPdfUri(String pdfUri) {
		this.pdfUri = pdfUri;
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

	@JsonIgnore
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<MetadataFieldGroup> getFields() {
		return fields;
	}

	public void setFields(List<MetadataFieldGroup> fields) {
		this.fields = fields;
	}
	
	public void addField(MetadataFieldGroup field) {
		fields.add(field);
	}
	
	public void removeField(MetadataFieldGroup field) {
		fields.remove(field);
	}
	
	public void clearFields() {
		fields = new ArrayList<MetadataFieldGroup>();
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
