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
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

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
    private Set<FieldProfile> profiles;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Document.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Document> documents;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> authorities;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> suggestors;

    private Boolean isLocked = false;

    private String repositoryUIUrlString;

    public Project() {
        profiles = new HashSet<FieldProfile>();
        documents = new HashSet<Document>();
        authorities = new HashSet<String>();
        suggestors = new HashSet<String>();
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

    public String getRepositoryUrlString() {
        return repositoryUIUrlString;
    }

    public void setRepositoryUIUrlString(String repositoryUIUrlString) {
        this.repositoryUIUrlString = repositoryUIUrlString;
    }

    public Set<FieldProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<FieldProfile> profiles) {
        this.profiles = profiles;
    }

    public void addProfile(FieldProfile profile) {
        profiles.add(profile);
    }

    public void removeProfile(FieldProfile profile) {
        profiles.remove(profile);
    }

    public void clearProfiles() {
        profiles = new HashSet<FieldProfile>();
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

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public Set<String> getSuggestors() {
        return suggestors;
    }

    public void setSuggestors(Set<String> suggestors) {
        this.suggestors = suggestors;
    }

}
