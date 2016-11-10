/* 
 * CustomMetadataFieldRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;

/**
 * 
 * 
 * @author
 *
 */
public interface MetadataFieldGroupRepoCustom {

    public MetadataFieldGroup create(Document document, MetadataFieldLabel label);

    public void delete(MetadataFieldGroup value);

    public void deleteAll();

}
