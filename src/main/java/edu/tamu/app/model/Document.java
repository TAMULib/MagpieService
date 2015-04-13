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
	
	/**
	 * Gets notes.
	 * 
	 * @return		String
	 */
	public String getNotes();

	/**
	 * Sets notes.
	 * 
	 * @param 		annotator			String
	 */
	public void setNotes(String notes);
	
	
}
