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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 
 * 
 * @author 
 *
 */
@Entity
public class Project {
	
	@Id
	private String name;
	
	private String repositoryUIUrlString;
	
	private Boolean isLocked = false;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<ProjectLabelProfile> profiles = new HashSet<ProjectLabelProfile>();
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval = false)
	private Set<Document> documents = new HashSet<Document>();
	
	public Project() {}
	
	public Project(String name) {
		this.name = name;
	}

	public Boolean getIsLocked() {
		return this.isLocked;
	}

	public void setIsLocked(Boolean status) {
		this.isLocked = status;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRepositoryUrlString() {
		return repositoryUIUrlString;
	}

	public void setRepositoryUIUrlString(String repositoryUIUrlString) {
		this.repositoryUIUrlString = repositoryUIUrlString;
	}

	public Set<ProjectLabelProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(Set<ProjectLabelProfile> profiles) {
		this.profiles = profiles;
	}
	
	public void addProfile(ProjectLabelProfile profile) {
		profiles.add(profile);
	}
	
	public void removeProfile(ProjectLabelProfile profile) {
		profiles.remove(profile);
	}
	
	public void clearProfiles() {
		profiles = new HashSet<ProjectLabelProfile>();
	}

	public Set<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<Document> documents) {
		this.documents = documents;
	}
	
	public void addDocument(Document document) {
		documents.add(document);
	}
	
	public void removeDocument(Document document) {
		documents.remove(document);
	}
	
	public void clearDocuments() {
		documents = new HashSet<Document>();
	}

}
