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
	public String getStatus();

	/**
	 * 
	 * @param status
	 */
	public void setStatus(String status);
	
	/**
	 * 
	 * @return
	 */
	public String getAnnotator();

	/**
	 * 
	 * @param annotator
	 */
	public void setAnnotator(String annotator);
	
	
}
