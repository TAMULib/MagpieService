package edu.tamu.app.model;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
public class ControlledVocabulary extends BaseEntity {

    // TODO: consider refactoring to name
    @Column(unique = true)
    private String value;

    @OneToMany(mappedBy = "cv", fetch = FetchType.EAGER, cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = MetadataFieldValue.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<MetadataFieldValue> values;

    @PrePersist
    @PreUpdate
    protected void sanitize() throws UnsupportedEncodingException {
        value = new String(value.replaceAll("[\u0000-\u001f]", "").getBytes(), "UTF-8");
    }

    public ControlledVocabulary() {
        values = new HashSet<MetadataFieldValue>();
    }

    public ControlledVocabulary(String value) {
        this();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonIgnore
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
