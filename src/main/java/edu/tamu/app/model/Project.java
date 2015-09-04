/* 
 * Project.java 
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
public class Project {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true)
	private String name;
	
	@OneToMany(mappedBy = "project", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, scope=MetadataField.class, property="id")
	@JsonIdentityReference(alwaysAsId=true)
	private List<ProjectFieldProfile> profiles = new ArrayList<ProjectFieldProfile>();
	
	@OneToMany(mappedBy = "project", cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, scope=Document.class, property="id")
	@JsonIdentityReference(alwaysAsId=true)
	private List<Document> documents = new ArrayList<Document>();
	
	public Project() { }
	
	public Project(String name) {
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

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
	public void addDocument(Document document) {
		documents.add(document);
	}
	
	public void removeDocument(Document document) {
		documents.remove(document);
	}
	
	public void clearDocuments() {
		documents = new ArrayList<Document>();
	}

}
