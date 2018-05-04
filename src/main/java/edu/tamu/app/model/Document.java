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

import edu.tamu.app.resolver.ProjectByNameResolver;
import edu.tamu.weaver.data.model.BaseEntity;

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
    private String path;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Project.class, resolver = ProjectByNameResolver.class, property = "name")
    @JsonIdentityReference(alwaysAsId = true)
    private Project project;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    private List<MetadataFieldGroup> fields;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    private List<PublishedLocation> publishedLocations;

    public Document() {
        fields = new ArrayList<MetadataFieldGroup>();
        publishedLocations = new ArrayList<PublishedLocation>();
    }

    public Document(Project project, String name, String path, String status) {
        this();
        setProject(project);
        setName(name);
        setPath(path);
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<MetadataFieldGroup> getFields() {
        return fields;
    }

    public void setFields(List<MetadataFieldGroup> fields) {
        this.fields = fields;
    }

    public void addField(MetadataFieldGroup field) {
        if (!fields.contains(field)) {
            fields.add(field);
        }
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
