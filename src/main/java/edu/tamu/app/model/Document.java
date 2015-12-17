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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
	
	private String publishedHandle;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Project.class, property = "name") 
	@JsonIdentityReference(alwaysAsId = true)
	private Project project;
	
	@OneToMany(mappedBy="document", cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REMOVE, CascadeType.REFRESH}, fetch = FetchType.EAGER, orphanRemoval = true)	
	private Set<MetadataFieldGroup> fields = new HashSet<MetadataFieldGroup>();
	
	public Document() 
	{ 
		this.publishedHandle = null;
	}
	
	public Document(Project project, String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status) {
		this.project = project;
		this.name = name;
		this.txtUri = txtUri;
		this.pdfUri = pdfUri;
		this.pdfPath = pdfPath;
		this.txtPath = txtPath;
		this.status = status;
		this.publishedHandle = null;
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

	public String getPublishedHandle() {
		return publishedHandle;
	}

	public void setPublishedHandle(String publishedUriString) {
		this.publishedHandle = publishedUriString;
	}

	@JsonIgnore
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<MetadataFieldGroup> getFields() {
		return fields;
	}

	public void setFields(Set<MetadataFieldGroup> fields) {
		this.fields = fields;
	}
	
	public void addField(MetadataFieldGroup field) {
		fields.add(field);
	}
	
	public void removeField(MetadataFieldGroup field) {
		fields.remove(field);
	}
	
	public void clearFields() {
		fields = new HashSet<MetadataFieldGroup>();
	}
	
}
