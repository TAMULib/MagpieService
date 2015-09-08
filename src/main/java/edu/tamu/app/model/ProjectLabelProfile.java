/* 
 * ProjectFieldProfile.java 
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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * 
 * 
 * @author 
 *
 */
@Entity
@Table
public class ProjectLabelProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String gloss;
	
	private boolean isRepeatable;
	
	private boolean isReadOnly;
	
	private boolean isHidden;
	
	private boolean isRequired;
	
	private InputType inputType;
	
	private String defaultValue;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = Project.class, property = "id") 
	@JsonIdentityReference(alwaysAsId = true)
	private Project project;
	
	@OneToMany(mappedBy="profile", fetch=FetchType.EAGER)	
	@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, scope = MetadataFieldLabel.class, property = "id")
	@JsonIdentityReference(alwaysAsId = true)
	private List<MetadataFieldLabel> labels = new ArrayList<MetadataFieldLabel>();
	
	public ProjectLabelProfile() { }

	public ProjectLabelProfile(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue) {		
		this.project = project;
		this.gloss = gloss;
		this.isReadOnly = isReadOnly == null ? false : isReadOnly;
		this.isHidden = isHidden == null ?  false : isHidden;
		this.isRepeatable = isRepeatable == null ? false : isRepeatable;
		this.isRequired = isRequired == null ? false : isRequired;
		this.inputType = inputType;
		this.defaultValue = defaultValue;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGloss() {
		return gloss;
	}

	public void setGloss(String gloss) {
		this.gloss = gloss;
	}

	public boolean isRepeatable() {
		return isRepeatable;
	}

	public void setRepeatable(boolean isRepeatable) {
		this.isRepeatable = isRepeatable;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired = isRequired;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	@JsonIgnore
	public List<MetadataFieldLabel> getLabels() {
		return labels;
	}

	public void setLabels(List<MetadataFieldLabel> labels) {
		this.labels = labels;
	}
	
	public void addLabel(MetadataFieldLabel label) {
		labels.add(label);
	}
	
	public void removeLabel(MetadataFieldLabel label) {
		labels.remove(label);
	}
	
	public void clearLabels() {
		labels = new ArrayList<MetadataFieldLabel>();
	}

}
