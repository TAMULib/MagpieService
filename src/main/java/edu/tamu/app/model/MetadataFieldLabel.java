/* 
 * MetadataFieldLabel.java 
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
public class MetadataFieldLabel {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String name;
	
	@OneToMany(mappedBy="label", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch=FetchType.EAGER)	
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, scope=ProjectFieldProfile.class, property="id")
	@JsonIdentityReference(alwaysAsId=true)
	private List<ProjectFieldProfile> profiles = new ArrayList<ProjectFieldProfile>();
	
	@OneToMany(mappedBy="label", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, scope=MetadataField.class, property="id")
	@JsonIdentityReference(alwaysAsId=true)
	private List<MetadataField> fields = new ArrayList<MetadataField>();
	
	public MetadataFieldLabel() { }
	
	public MetadataFieldLabel(String name) {
		this.name = name;
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

	public List<ProjectFieldProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<ProjectFieldProfile> profiles) {
		this.profiles = profiles;
	}
	
	public void addProfile(ProjectFieldProfile profile) {
		profiles.add(profile);
	}
	
	public void removeProfile(ProjectFieldProfile profile) {
		profiles.remove(profile);
	}
	
	public void clearProfiles() {
		profiles = new ArrayList<ProjectFieldProfile>();
	}

	public List<MetadataField> getFields() {
		return fields;
	}

	public void setFields(List<MetadataField> fields) {
		this.fields = fields;
	}
	
	public void addField(MetadataField field) {
		fields.add(field);
	}
	
	public void removeField(MetadataField field) {
		fields.remove(field);
	}
	
	public void clearFields() {
		fields = new ArrayList<MetadataField>();
	}
	
}
