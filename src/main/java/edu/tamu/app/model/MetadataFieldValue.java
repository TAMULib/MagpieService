/* 
 * MetadataFieldValue.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * 
 * @author 
 *
 */
@Entity
@Table
public class MetadataFieldValue {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	private MetadataField field;
	
	@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, optional = true, fetch = FetchType.EAGER)
	private ControlledVocabulary cv;
	
	@Column(nullable = true)
	private String value;
	
	public MetadataFieldValue() { }
	
	public MetadataFieldValue(ControlledVocabulary cv, MetadataField field) { 
		this.cv = cv;
		this.field = field;
	}
	
	public MetadataFieldValue(String value, MetadataField field) { 
		this.value = value;
		this.field = field;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MetadataField getField() {
		return field;
	}

	public void setField(MetadataField field) {
		this.field = field;
	}

	public ControlledVocabulary getCv() {
		return cv;
	}

	public void setCv(ControlledVocabulary cv) {
		this.cv = cv;
	}

	public String getValue() {
		if(value == null) return cv.getValue();
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
