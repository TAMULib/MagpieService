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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
	private String name;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<ProjectLabelProfile> profiles = new ArrayList<ProjectLabelProfile>();
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval = false)
	private List<Document> documents = new ArrayList<Document>();
	
	public Project() { }
	
	public Project(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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