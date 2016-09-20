/* 
 * MetadataFieldLabel.java 
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
public class MetadataFieldLabel extends BaseEntity {

    @Column(unique = true)
    private String name;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH }, fetch = FetchType.EAGER)
    private ProjectProfile profile;

    @OneToMany(mappedBy = "label", fetch = FetchType.EAGER)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = MetadataFieldGroup.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<MetadataFieldGroup> fields;

    public MetadataFieldLabel() {
        fields = new HashSet<MetadataFieldGroup>();
    }

    public MetadataFieldLabel(String name, ProjectProfile profile) {
        this();
        this.name = name;
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectProfile getProfile() {
        return profile;
    }

    public void setProfile(ProjectProfile profile) {
        this.profile = profile;
    }

    public Set<MetadataFieldGroup> getFields() {
        return fields;
    }

    public void setFields(Set<MetadataFieldGroup> fields) {
        this.fields = fields;
    }

    public void addField(MetadataFieldGroup field) {
        fields.add(field);
    }

    public void removeField(MetadataFieldGroup field) {
        fields.remove(field);
    }

    public void clearFields() {
        fields = new HashSet<MetadataFieldGroup>();
    }

}
