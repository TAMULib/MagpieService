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

/**
 * Document interface.
 * 
 * @author
 *
 */
public interface MetadataField {
	
	/**
	 * 	
	 * @return
	 */
	public Long getId();

	/**
	 * 
	 * @param id
	 */
	public void setId(Long id);
	
	/**
	 * 
	 * @return
	 */
	public String getFilename();

	/**
	 * 
	 * @param filename
	 */
	public void setFilename(String filename);

	/**
	 * 
	 * @return
	 */
	public String getLabel();

	/**
	 * 
	 * @param label
	 */
	public void setLabel(String label);

	/**
	 * 
	 * @return
	 */
	public String getValue();

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value);

	/**
	 * 
	 * @return
	 */
	public boolean isRepeatable();

	/**
	 * 
	 * @param isRepeatable
	 */
	public void setRepeatable(boolean isRepeatable);
	
	/**
	 * 
	 * @return
	 */
	public int getIndex();

	/**
	 * 
	 * @param index
	 */
	public void setIndex(int index);
	
}
