/* 
 * MetadataFields.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

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
public class MetadataFieldGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Document.class, property = "id") 
	@JsonIdentityReference(alwaysAsId = true)
	private Document document;
	
	// probably should be CascadeType.ALL
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	private MetadataFieldLabel label;
	
	@OneToMany(mappedBy="field", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)	
	private List<MetadataFieldValue> values = new ArrayList<MetadataFieldValue>();
	
	public MetadataFieldGroup() { }
	
	public MetadataFieldGroup(MetadataFieldLabel label) {
		this.label = label;
	}
	
	public MetadataFieldGroup(Document document, MetadataFieldLabel label) {
		this.document = document;
		this.label = label;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public MetadataFieldLabel getLabel() {
		return label;
	}

	public void setLabel(MetadataFieldLabel label) {
		this.label = label;
	}

	public List<MetadataFieldValue> getValues() {
		return values;
	}

	public void setValues(List<MetadataFieldValue> values) {
		this.values = values;
	}
	
	public void addValue(MetadataFieldValue value) {
		values.add(value);
	}
	
	public void removeValue(MetadataFieldValue value) {
		values.remove(value);
	}
	
	public void clearValues() {
		values = new ArrayList<MetadataFieldValue>();
	}
	
}
