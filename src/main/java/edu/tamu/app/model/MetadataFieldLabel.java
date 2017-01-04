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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.tamu.framework.model.BaseEntity;

/**
 * 
 * 
 * @author
 *
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "name", "profile_id" }))
public class MetadataFieldLabel extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private FieldProfile profile;

    public MetadataFieldLabel() {
    }

    public MetadataFieldLabel(String name, FieldProfile profile) {
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

    public FieldProfile getProfile() {
        return profile;
    }

    public void setProfile(FieldProfile profile) {
        this.profile = profile;
    }

}
