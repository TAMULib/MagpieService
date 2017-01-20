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
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.framework.model.BaseEntity;

/**
 * 
 * 
 * @author
 *
 */
@Entity
public class Project extends BaseEntity {

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FieldProfile> profiles;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Document.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private List<Document> documents;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<ProjectAuthority> authorities;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<ProjectSuggestor> suggestors;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<ProjectRepository> repositories;

    private Boolean isLocked = false;

    public Project() {
        setProfiles(new ArrayList<FieldProfile>());
        setDocuments(new ArrayList<Document>());
        setAuthorities(new ArrayList<ProjectAuthority>());
        setSuggestors(new ArrayList<ProjectSuggestor>());
        setRepositories(new ArrayList<ProjectRepository>());
    }

    public Project(String name) {
        this();
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

    public List<FieldProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<FieldProfile> profiles) {
        this.profiles = profiles;
    }

    public void addProfile(FieldProfile profile) {
        profiles.add(profile);
    }

    public void removeProfile(FieldProfile profile) {
        profiles.remove(profile);
    }

    public void clearProfiles() {
        setProfiles(new ArrayList<FieldProfile>());
    }

    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Returns all documents considered ready for publishing.
     * @return List<Document>
     */
    public List<Document> getPublishableDocuments() {
    	List<Document> publishableDocuments = new ArrayList<Document>();
    	for (Document document:this.getDocuments()) {
    		if (document.getStatus().equals("Accepted")) {
    			publishableDocuments.add(document);
    		}
    	}
        return publishableDocuments;
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
        setDocuments(new ArrayList<Document>());
    }

    public List<ProjectAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<ProjectAuthority> authorities) {
        this.authorities = authorities;
    }

    public void addAuthority(ProjectAuthority authority) {
        authorities.add(authority);
    }

    public void removeAuthority(ProjectAuthority authority) {
        authorities.remove(authority);
    }

    public void clearAuthorities() {
        setAuthorities(new ArrayList<ProjectAuthority>());
    }

    public List<ProjectSuggestor> getSuggestors() {
        return suggestors;
    }

    public void setSuggestors(List<ProjectSuggestor> suggestors) {
        this.suggestors = suggestors;
    }

    public void addSuggestor(ProjectSuggestor suggestor) {
        suggestors.add(suggestor);
    }

    public void removeSuggestor(ProjectSuggestor suggestor) {
        suggestors.remove(suggestor);
    }

    public void clearSuggestors() {
        setSuggestors(new ArrayList<ProjectSuggestor>());
    }

    public List<ProjectRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<ProjectRepository> repositories) {
        this.repositories = repositories;
    }
    
    /**
     * Matches by repositoryId a Repository from the Project's list of Repositories and returns it
     *  
     * @param repositoryId
     * @return ProjectRepository
     */
    public ProjectRepository getRepositoryById(Long repositoryId) {
    	for (ProjectRepository repository:this.getRepositories()) {
    		if (repository.getId() == repositoryId) {
    			return repository;
    		}
    	}    	
    	return null;
    }

    public void addRepository(ProjectRepository repository) {
        repositories.add(repository);
    }

    public void removeRepository(ProjectRepository repository) {
        repositories.remove(repository);
    }

    public void clearRepositories() {
        setRepositories(new ArrayList<ProjectRepository>());
    }

}
