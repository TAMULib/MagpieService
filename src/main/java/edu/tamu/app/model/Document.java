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

import java.util.List;

import edu.tamu.app.model.impl.MetadataLabelImpl;

/**
 * Document interface.
 * 
 * @author
 *
 */
public interface Document {

	public Long getId();
	
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
	
	/**
	 * Gets metadata labels.
	 * 
	 * @return		List<MetadataLabelImpl>
	 */
	public List<MetadataLabelImpl> getMetadataLabels();

	/**
	 * Sets metadata labels.
	 * 
	 * @param 		metadataLabels		List<MetadataLabelImpl>
	 */
	public void setMetadataLabels(List<MetadataLabelImpl> metadataLabels);
	
}
