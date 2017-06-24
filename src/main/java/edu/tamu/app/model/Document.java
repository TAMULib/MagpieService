/* 
 * Document.java 
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "project_id" }))
public class Document extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(nullable = true)
    private String annotator;

    @Column(nullable = true)
    private String notes;

    @Column(nullable = true)
    private String documentPath;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Project.class, property = "name")
    @JsonIdentityReference(alwaysAsId = true)
    private Project project;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<Resource> resources;

    @OneToMany(mappedBy = "document", fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE }, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<MetadataFieldGroup> fields;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<PublishedLocation> publishedLocations;

    public Document() {
    	resources = new ArrayList<Resource>();
        fields = new ArrayList<MetadataFieldGroup>();
        publishedLocations = new ArrayList<PublishedLocation>();
    }

    public Document(Project project, String name, String documentPath, String status) {
        this();
        setProject(project);
        setName(name);
        setDocumentPath(documentPath);
        setStatus(status);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAnnotator() {
        return annotator;
    }

    public void setAnnotator(String annotator) {
        this.annotator = annotator;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    
    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public void addResource(Resource resource) {
    	resources.add(resource);
    }

    public void removeResource(Resource resource) {
    	resources.remove(resource);
    }

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
        fields.clear();
    }

    public List<PublishedLocation> getPublishedLocations() {
        return publishedLocations;
    }

    public void setPublishedLocations(List<PublishedLocation> publishedLocations) {
        this.publishedLocations = publishedLocations;
    }

    public void addPublishedLocation(PublishedLocation publishedLocation) {
        if (!publishedLocations.contains(publishedLocation)) {
            publishedLocations.add(publishedLocation);
        }
    }

    public void removePublishedLocation(PublishedLocation publishedLocation) {
        publishedLocations.remove(publishedLocation);
    }

    public void clearPublishedLocations() {
        publishedLocations.clear();
    }

    public MetadataFieldGroup getFieldByLabel(String labelName) {
        MetadataFieldGroup targetField = null;
        for (MetadataFieldGroup field : fields) {
            if (field.getLabel().getName().equals(labelName)) {
                targetField = field;
                break;
            }
        }
        return targetField;
    }

}
