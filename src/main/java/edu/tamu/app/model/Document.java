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
    private String txtUri;

    @Column(nullable = true)
    private String pdfUri;

    @Column(nullable = true)
    private String pdfPath;

    @Column(nullable = true)
    private String txtPath;

    @Column(nullable = true)
    private String publishedUriString;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Project.class, property = "name")
    @JsonIdentityReference(alwaysAsId = true)
    private Project project;

    @OneToMany(mappedBy = "document", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MetadataFieldGroup> fields;

    public Document() {
        publishedUriString = null;
        fields = new ArrayList<MetadataFieldGroup>();
    }

    public Document(Project project, String name, String txtUri, String pdfUri, String txtPath, String pdfPath, String status) {
        this();
        this.project = project;
        this.name = name;
        this.txtUri = txtUri;
        this.pdfUri = pdfUri;
        this.pdfPath = pdfPath;
        this.txtPath = txtPath;
        this.status = status;
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

    public String getTxtUri() {
        return txtUri;
    }

    public void setTxtUri(String txtUri) {
        this.txtUri = txtUri;
    }

    public String getPdfUri() {
        return pdfUri;
    }

    public void setPdfUri(String pdfUri) {
        this.pdfUri = pdfUri;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getTxtPath() {
        return txtPath;
    }

    public void setTxtPath(String txtPath) {
        this.txtPath = txtPath;
    }

    public String getPublishedUriString() {
        return publishedUriString;
    }

    public void setPublishedUriString(String publishedUriString) {
        this.publishedUriString = publishedUriString;
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
        fields.add(field);
    }

    public void removeField(MetadataFieldGroup field) {
        fields.remove(field);
    }

    public void clearFields() {
        fields = new ArrayList<MetadataFieldGroup>();
    }
}
