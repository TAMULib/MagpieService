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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class ControlledVocabulary {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true)
	private String value;
	
	@OneToMany(mappedBy="cv", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch=FetchType.EAGER)	
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, scope=MetadataFieldValue.class, property="id")
	@JsonIdentityReference(alwaysAsId=true)
	private List<MetadataFieldValue> values = new ArrayList<MetadataFieldValue>();
	
	public ControlledVocabulary() { }
	
	public ControlledVocabulary(String value) {
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
