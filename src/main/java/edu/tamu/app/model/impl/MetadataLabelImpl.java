/* 
 * MetadataLabelImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.impl;

import javax.persistence.Embeddable;

import edu.tamu.app.model.InputType;
import edu.tamu.app.model.MetadataLabel;

/**
 * Implementation of metadatalabel object.
 * 
 * @author 
 *
 */
@Embeddable
public class MetadataLabelImpl implements MetadataLabel {
	
	private String label;
	private String gloss;
	
	private boolean isRepeatable;
	
	private InputType inputType;
	private String defaultValue;
	
	/**
	 * Default constructor.
	 * 
	 */
	public MetadataLabelImpl() {
		super();
	}
	
	/**
	 * 
	 * @param label
	 * @param gloss
	 * @param isRepeatable
	 * @param inputType
	 * @param defaultValue
	 */
	public MetadataLabelImpl(String label, String gloss, boolean isRepeatable, InputType inputType, String defaultValue) {
		
		this.label = label;
		this.gloss = gloss;
		this.isRepeatable = isRepeatable;
		this.inputType = inputType;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Gets label.
	 * 
	 * @return 		String
	 * 
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets label.
	 * 
	 * @param 		label			String
	 * 
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Gets gloss.
	 * 
	 * @return 		String
	 * 
	 */
	public String getGloss() {
		return gloss;
	}

	/**
	 * Sets gloss.
	 * 
	 * @param 		gloss			String
	 * 
	 */
	public void setGloss(String gloss) {
		this.gloss = gloss;
	}

	/**
	 * Checks if repeatable.
	 * 
	 * @return 		boolean
	 * 
	 */
	public boolean isRepeatable() {
		return isRepeatable;
	}

	/**
	 * Sets repeatable.
	 * 
	 * @param 		isRepeatable		boolean
	 * 
	 */
	public void setRepeatable(boolean isRepeatable) {
		this.isRepeatable = isRepeatable;
	}

	/**
	 * Gets input type.
	 * 
	 * @return 		InputType
	 * 
	 */
	public InputType getInputType() {
		return inputType;
	}

	/**
	 * Sets input type.
	 * 
	 * @param 		inputType		InputType
	 * 
	 */
	public void setInputType(InputType inputType) {
		this.inputType = inputType;
	}

	/**
	 * Gets default value.
	 * 
	 * @return 		defaultValue
	 * 
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets default value.
	 * 
	 * @param 		defaultValue 	String
	 * 
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
