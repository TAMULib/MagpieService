/* 
 * ResourceRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.model.repo.custom.ResourceRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

/**
 *
 * 
 * @author
 *
 */
public class ResourceRepoImpl extends AbstractWeaverRepoImpl<Resource, ResourceRepo> implements ResourceRepoCustom {

    @Autowired
    private ResourceRepo resourceRepo;

    @Override
    public synchronized Resource create(Document document, String name, String path, String mimeType) {
        Resource resource = resourceRepo.findByDocumentNameAndName(document.getName(), name);
        if (resource == null) {
            resource = resourceRepo.save(new Resource(document, name, path, mimeType));
        }
        return resource;
    }

    public List<Resource> findAllByDocumentNameAndMimeType(String documentName, String... mimeTypes) {
        return resourceRepo.findAllByDocumentName(documentName).stream().filter(resource -> Arrays.asList(mimeTypes).contains(resource.getMimeType())).collect(Collectors.toList());
    }

    @Override
    protected String getChannel() {
        return "/channel/resource";
    }

}
