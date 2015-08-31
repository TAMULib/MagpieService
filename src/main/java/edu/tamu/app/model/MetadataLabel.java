/* 
 * MetadataLabel.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

/**
 * MetadataLabel interface.
 * 
 * @author
 *
 */
public interface MetadataLabel {

	/**
	 * Gets label.
	 * 
	 * @return		String
	 * 
	 */
	public String getLabel();

	/**
	 * Sets label.
	 * 
	 * @param 		label			String
	 * 
	 */
	public void setLabel(String label);

	/**
	 * Gets gloss.
	 * 
	 * @return		String
	 * 
	 */
	public String getGloss();

	/**
	 * Sets gloss.
	 * 
	 * @param 		gloss			String
	 * 
	 */
	public void setGloss(String gloss);

	/**
	 * Checks if repeatable.
	 * 
	 * @return		boolean
	 * 
	 */
	public boolean isRepeatable();

	/**
	 * Set repeatable.
	 * 
	 * @param 		isRepeatable	boolean
	 * 
	 */
	public void setRepeatable(boolean isRepeatable);

	/**
	 * Checks if readOnly.
	 * 
	 * @return		boolean
	 * 
	 */
	public boolean isReadOnly();

	/**
	 * Set repeatable.
	 * 
	 * @param 		isRepeatable	boolean
	 * 
	 */
	public void setReadOnly(boolean isReadOnly);
	
	/**
	 * Checks if required.
	 * 
	 * @return		boolean
	 * 
	 */
	public boolean isRequired();

	/**
	 * Set repeatable.
	 * 
	 * @param 		isRepeatable	boolean
	 * 
	 */
	public void setRequired(boolean isRequired);
	
	/**
	 * Gets input type.
	 * 
	 * @return		InputType
	 * 
	 */
	public InputType getInputType();

	/**
	 * Sets input type.
	 * 
	 * @param 		inputType		InputType
	 * 
	 */
	public void setInputType(InputType inputType);
	
	/**
	 * Gets default value.
	 * 
	 * @return 		defaultValue
	 * 
	 */
	public String getDefaultValue();

	/**
	 * Sets default value.
	 * 
	 * @param 		defaultValue 	String
	 * 
	 */
	public void setDefaultValue(String defaultValue);

}
