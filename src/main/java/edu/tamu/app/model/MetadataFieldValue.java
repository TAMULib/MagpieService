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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
public class MetadataFieldValue {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = MetadataFieldGroup.class, property = "id") 
	@JsonIdentityReference(alwaysAsId = true)
	private MetadataFieldGroup field;
	
	@ManyToOne(optional = true, cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	private ControlledVocabulary cv;
	
	@Column(columnDefinition = "TEXT", nullable = true)
	private String value;
	
	@PrePersist
	@PreUpdate
	protected void sanitize() {
		if(value != null) {
			value = value.replaceAll("[\u0000-\u001f]", "");
		}
	}
	
	public MetadataFieldValue() { }
	
	public MetadataFieldValue(ControlledVocabulary cv, MetadataFieldGroup field) { 
		this.cv = cv;
		this.field = field;
	}
	
	public MetadataFieldValue(String value, MetadataFieldGroup field) { 
		this.value = value;
		this.field = field;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public MetadataFieldGroup getField() {
		return field;
	}

	public void setField(MetadataFieldGroup field) {
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