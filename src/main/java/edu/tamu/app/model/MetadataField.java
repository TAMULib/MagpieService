/* 
 * Document.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model;

import edu.tamu.app.model.impl.DocumentProfileImpl;

/**
 * Document interface.
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

	/**
	 * Gets label.
	 * 
	 * @return		String
	 */
	public String getLabel();

	/**
	 * Sets label.
	 * 
	 * @param 		label			String
	 */
	public void setLabel(String label);

	/**
	 * Gets value.
	 * 
	 * @return		String
	 */
	public String getValue();

	/**
	 * Sets value.
	 * 
	 * @param 		value			String
	 */
	public void setValue(String value);

	/**
	 * Gets isRepeatable.
	 * 
	 * @return		boolean
	 */
	public boolean getIsRepeatable();

	/**
	 * Sets isRepeatable.
	 * 
	 * @param 		isRepeatable	boolean
	 */
	public void setRepeatable(boolean isRepeatable);
	
	/**
	 * Gets index.
	 * 
	 * @return		int
	 */
	public int getIndex();

	/**
	 * Sets index.
	 * 
	 * @param 		index			int
	 */
	public void setIndex(int index);
	
	/**
	 * Gets status.
	 * 
	 * @return		String
	 */
	public String getStatus();

	/**
	 * Sets status.
	 * 
	 * @param 		status			String
	 */
	public void setStatus(String status);
	
	public DocumentProfileImpl getProfile();

	public void setProfile(DocumentProfileImpl profile);
	
}
