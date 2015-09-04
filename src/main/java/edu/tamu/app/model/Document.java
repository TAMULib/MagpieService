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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
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
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	private String status;
	
	private String annotator;
	
	private String notes;

	private String txtUri;

	private String pdfUri;
	
	private String pdfPath;
	
	private String txtPath;
	
	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	private Project project;
	
	@OneToMany(mappedBy="document", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER, orphanRemoval = true)
	@Fetch(FetchMode.SELECT)
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, scope=MetadataField.class, property="id")
	@JsonIdentityReference(alwaysAsId=false)
	private List<MetadataField> fields = new ArrayList<MetadataField>();
	
	public Document() { }
	
	public Document(String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status, List<MetadataField> fields) {
		this.name = name;
		this.txtUri = txtUri;
		this.pdfUri = pdfUri;
		this.pdfPath = pdfPath;
		this.txtPath = txtPath;
		this.status = status;
		this.fields = fields;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<MetadataField> getMetadataFields() {
		return fields;
	}

	public void setMetadataFields(List<MetadataField> fields) {
		this.fields = fields;
	}
	
	public void addMetadataField(MetadataField field) {
		fields.add(field);
	}
	
	public void removeMetadataField(MetadataField field) {
		fields.remove(field);
	}
	
	public void clearMetadataFields() {
		fields = new ArrayList<MetadataField>();
	}
	
	/* 
	 * TODO: 
	 * 
	 * template hardcoded classpath in application properties
	 * or use classpath
	 * 
	 */

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
