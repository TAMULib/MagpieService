/* 
 * ResourceRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import java.util.List;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Resource;

/**
 * 
 * 
 * @author
 *
 */
public interface ResourceRepoCustom {

    public Resource create(Document document, String name, String path, String mimeType);

    public List<Resource> findAllByDocumentNameAndMimeType(String documentName, String... mimeTypes);

}
