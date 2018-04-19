package edu.tamu.app.model;

import java.util.HashSet;
import java.util.Set;

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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import edu.tamu.weaver.data.model.BaseEntity;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "gloss", "project_id" }) })
public class FieldProfile extends BaseEntity {

    @Column(nullable = false)
    private String gloss;

    @Column(nullable = false)
    private Boolean repeatable;

    @Column(nullable = false)
    private Boolean readOnly;

    @Column(nullable = false)
    private Boolean hidden;

    @Column(nullable = false)
    private Boolean required;

    @Column(nullable = false)
    private InputType inputType;

    @Column(nullable = true)
    private String defaultValue;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Project.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Project project;

    @OneToMany(mappedBy = "profile", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = MetadataFieldLabel.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Set<MetadataFieldLabel> labels;

    public FieldProfile() {
        labels = new HashSet<MetadataFieldLabel>();
        repeatable = false;
        readOnly = false;
        hidden = false;
        required = false;
    }

    public FieldProfile(Project project, String gloss, Boolean repeatable, Boolean readOnly, Boolean hidden, Boolean required, InputType inputType, String defaultValue) {
        this();
        this.project = project;
        this.gloss = gloss;
        this.repeatable = repeatable == null ? this.repeatable : repeatable;
        this.readOnly = readOnly == null ? this.readOnly : readOnly;
        this.hidden = hidden == null ? this.hidden : hidden;
        this.required = required == null ? this.required : required;
        this.inputType = inputType;
        this.defaultValue = defaultValue;
    }

    public String getGloss() {
        return gloss;
    }

    public void setGloss(String gloss) {
        this.gloss = gloss;
    }

    public Boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(Boolean isRepeatable) {
        this.repeatable = isRepeatable;
    }

    public Boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean isReadOnly) {
        this.readOnly = isReadOnly;
    }

    public Boolean isHidden() {
        return hidden;
    }

    public void setHidden(Boolean isHidden) {
        this.hidden = isHidden;
    }

    public Boolean isRequired() {
        return required;
    }

    public void setRequired(Boolean isRequired) {
        this.required = isRequired;
    }

    public InputType getInputType() {
        return inputType;
    }

    public void setInputType(InputType inputType) {
        this.inputType = inputType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JsonIgnore
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<MetadataFieldLabel> getLabels() {
        return labels;
    }

    public void setLabels(Set<MetadataFieldLabel> labels) {
        this.labels = labels;
    }

    public void addLabel(MetadataFieldLabel label) {
        labels.add(label);
    }

    public void removeLabel(MetadataFieldLabel label) {
        labels.remove(label);
    }

    public void clearLabels() {
        labels = new HashSet<MetadataFieldLabel>();
    }

}
