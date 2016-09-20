/* 
 * MetadataFields.java 
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
import javax.persistence.ManyToOne;
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
public class MetadataFieldGroup extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Document.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Document document;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    private MetadataFieldLabel label;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<MetadataFieldValue> values;

    public MetadataFieldGroup() {
        values = new HashSet<MetadataFieldValue>();
    }

    public MetadataFieldGroup(MetadataFieldLabel label) {
        this();
        this.label = label;
    }

    public MetadataFieldGroup(Document document, MetadataFieldLabel label) {
        this(label);
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public MetadataFieldLabel getLabel() {
        return label;
    }

    public void setLabel(MetadataFieldLabel label) {
        this.label = label;
    }

    public Set<MetadataFieldValue> getValues() {
        return values;
    }

    public void setValues(Set<MetadataFieldValue> values) {
        this.values = values;
    }

    public void addValue(MetadataFieldValue value) {
        values.add(value);
    }

    public void removeValue(MetadataFieldValue value) {
        values.remove(value);
    }

    public void clearValues() {
        values = new HashSet<MetadataFieldValue>();
    }

}
