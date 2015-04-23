/* 
 * MetadataField.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import java.util.List;

/**
 * MetadataField interface.
 * 
 * @author
 *
 */
public interface MetadataField {
	
	/**
	 * 	Gets id.
	 * 
	 * @return		Long
	 */
	public Long getId();

	/**
	 * Sets id.
	 * 
	 * @param 		id				Long
	 */
	public void setId(Long id);
	
	/**
	 * Gets name.
	 * 
	 * @return		String
	 */
	public String getName();

	/**
	 * Sets name.
	 * 
	 * @param 		name			String
	 */
	public void setName(String name);
	
	public String getLabel();

	public void setLabel(String label);


	/**
	 * Gets value.
	 * 
	 * @return		String
	 */
	public List<String> getValues();

	/**
	 * Sets value.
	 * 
	 * @param 		value			String
	 */
	public void setValues(List<String> values);

	
}
