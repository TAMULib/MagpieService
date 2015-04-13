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
	 * Gets pdf uri.
	 * 
	 * @return		String
	 */
	public String getPdfUri();

	/**
	 * Sets pdf uri.
	 * 
	 * @param 		uri					String
	 */
	public void setPdfUri(String uri);
	
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
