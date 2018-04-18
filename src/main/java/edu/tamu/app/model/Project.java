package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
public class Project extends BaseEntity {

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE }, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<FieldProfile> profiles;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE }, orphanRemoval = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Document.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @Fetch(FetchMode.SELECT)
    private List<Document> documents;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @Fetch(FetchMode.SELECT)
    private List<ProjectAuthority> authorities;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @Fetch(FetchMode.SELECT)
    private List<ProjectSuggestor> suggestors;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @Fetch(FetchMode.SELECT)
    private List<ProjectRepository> repositories;

    @Enumerated(EnumType.STRING)
    private IngestType ingestType;

    @Column
    private boolean locked = false;

    @Column
    private boolean headless = false;

    public Project() {
        setProfiles(new ArrayList<FieldProfile>());
        setDocuments(new ArrayList<Document>());
        setAuthorities(new ArrayList<ProjectAuthority>());
        setSuggestors(new ArrayList<ProjectSuggestor>());
        setRepositories(new ArrayList<ProjectRepository>());
    }

    public Project(String name, IngestType ingestType, boolean headless) {
        this();
        this.name = name;
        this.ingestType = ingestType;
        this.headless = headless;
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

    public IngestType getIngestType() {
        return ingestType;
    }

    public void setIngestType(IngestType ingestType) {
        this.ingestType = ingestType;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Returns all documents considered ready for publishing.
     * 
     * @return List<Document>
     */
    public List<Document> getPublishableDocuments() {
        List<Document> publishableDocuments = new ArrayList<Document>();
        for (Document document : this.getDocuments()) {
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
        for (ProjectRepository repository : this.getRepositories()) {
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

    public boolean hasProfileWithLabel(String label) {
        boolean hasIt = false;
        for (FieldProfile fp : getProfiles()) {
            for (MetadataFieldLabel fpLabel : fp.getLabels()) {
                if (fpLabel.getName().equals(label)) {
                    hasIt = true;
                    break;
                }
            }
        }
        return hasIt;
    }

}
