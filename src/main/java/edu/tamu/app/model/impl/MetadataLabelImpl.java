package edu.tamu.app.model.impl;

import javax.persistence.Embeddable;

import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataLabel;

@Embeddable
public class MetadataLabelImpl implements MetadataLabel {
	
	private String label;
	private String gloss;
	
	private boolean isRepeatable;
	
	private InputType inputType;
	
	public MetadataLabelImpl() {}
	
	public MetadataLabelImpl(String label, String gloss, boolean isRepeatable, InputType inputType) {
		
		this.label = label;
		this.gloss = gloss;
		this.isRepeatable = isRepeatable;
		this.inputType = inputType;
		
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
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

	public InputType getInputType() {
		return inputType;
	}

	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}

}
