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
public class MetadataFieldLabel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private ProjectFieldProfile profile;

	public MetadataFieldLabel() { }
	
	public MetadataFieldLabel(String name) {
		this.name = name;
	}
	
	public MetadataFieldLabel(String name, ProjectFieldProfile profile) {
		this.name = name;
		this.profile = profile;
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

	public ProjectFieldProfile getProfile() {
		return profile;
	}

	public void setProfile(ProjectFieldProfile profile) {
		this.profile = profile;
	}
	
}
