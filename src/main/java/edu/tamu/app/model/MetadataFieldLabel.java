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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class MetadataFieldLabel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String name;
	
	@OneToMany(mappedBy="label", fetch=FetchType.EAGER)	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = ProjectLabelProfile.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private List<ProjectLabelProfile> profiles = new ArrayList<ProjectLabelProfile>();
	
	@OneToMany(mappedBy="label", fetch=FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = MetadataFieldGroup.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private List<MetadataFieldGroup> fields = new ArrayList<MetadataFieldGroup>();
	
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

	@JsonIgnore
	public List<ProjectLabelProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<ProjectLabelProfile> profiles) {
		this.profiles = profiles;
	}
	
	public void addProfile(ProjectLabelProfile profile) {
		profiles.add(profile);
	}
	
	public void removeProfile(ProjectLabelProfile profile) {
		profiles.remove(profile);
	}
	
	public void clearProfiles() {
		profiles = new ArrayList<ProjectLabelProfile>();
	}

	@JsonIgnore
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
	
}
