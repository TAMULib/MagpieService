/* 
 * MetadataFieldLabelRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldLabel;

/**
 * 
 * 
 * @author
 *
 */
public interface MetadataFieldLabelRepoCustom {

	public MetadataFieldLabel create(String name, FieldProfile profile);

}
