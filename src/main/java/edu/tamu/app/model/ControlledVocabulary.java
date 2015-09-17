/* 
 * ControlledVocabulary.java 
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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

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
public class ControlledVocabulary {

	@Id
	private String value;
	
	@OneToMany(mappedBy="cv", fetch = FetchType.EAGER)	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = MetadataFieldValue.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private Set<MetadataFieldValue> values = new HashSet<MetadataFieldValue>();
	
	@PrePersist
	@PreUpdate
	protected void sanitize() {
		value = value.replaceAll("[\u0000-\u001f]", "");
	}
	
	public ControlledVocabulary() { }
	
	public ControlledVocabulary(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@JsonIgnore
	public Set<MetadataFieldValue> getValues() {
		return values;
	}

	public void setValues(Set<MetadataFieldValue> values) {
		this.values = values;
	}

	public void addValue(MetadataFieldValue value) {
		values.add(value);
	}
	
	public void removeValue(MetadataFieldValue value) {
		values.remove(value);
	}
	
	public void clearValues() {
		values = new HashSet<MetadataFieldValue>();
	}
	
}
