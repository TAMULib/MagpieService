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
public interface Document {

	/**
	 * Gets filename.
	 * 
	 * @return		String
	 */
	public String getFilename();

	/**
	 * Sets filename.
	 * 
	 * @param 		filename			String
	 */
	public void setFilename(String filename);
	
	/**
	 * Gets status.
	 * 
	 * @return		String
	 */
	public String getStatus();

	/**
	 * Sets status.
	 * 
	 * @param 		status				String
	 */
	public void setStatus(String status);
	
	/**
	 * Gets annotator.
	 * 
	 * @return		String
	 */
	public String getAnnotator();

	/**
	 * Sets annotator.
	 * 
	 * @param 		annotator			String
	 */
	public void setAnnotator(String annotator);
	
	
}
