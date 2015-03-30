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

import java.io.File;

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
	public File getFile();

	/**
	 * 
	 * @param file
	 */
	public void setFile(File file);
	
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
	
}
