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
public class MetadataFieldLabel {

	@Id
	private String name;
	
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	private ProjectLabelProfile profile;
		
	@OneToMany(mappedBy="label", fetch=FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = MetadataFieldGroup.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private List<MetadataFieldGroup> fields = new ArrayList<MetadataFieldGroup>();
	
	public MetadataFieldLabel() { }
	
	public MetadataFieldLabel(String name, ProjectLabelProfile profile) {
		this.name = name;
		this.profile = profile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProjectLabelProfile getProfile() {
		return profile;
	}

	public void setProfile(ProjectLabelProfile profile) {
		this.profile = profile;
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
