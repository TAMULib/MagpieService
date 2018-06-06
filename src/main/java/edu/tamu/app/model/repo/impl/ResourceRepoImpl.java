package edu.tamu.app.model.repo.impl;

import static edu.tamu.app.Initialization.ASSETS_PATH;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.repo.ResourceRepo;
import edu.tamu.app.model.repo.custom.ResourceRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ResourceRepoImpl extends AbstractWeaverRepoImpl<Resource, ResourceRepo> implements ResourceRepoCustom {

    @Autowired
    private ResourceRepo resourceRepo;

    @Override
    public synchronized Resource create(Document document, String name, String path, String mimeType) {
        Resource resource = resourceRepo.findByDocumentProjectNameAndDocumentNameAndName(document.getProject().getName(), document.getName(), name);
        if (resource == null) {
            resource = resourceRepo.save(new Resource(document, name, path.replace(ASSETS_PATH, ""), mimeType));
        }
        return resource;
    }

    public List<Resource> findAllByDocumentProjectNameAndDocumentNameAndMimeType(String projectName, String documentName, String... mimeTypes) {
        return resourceRepo.findAllByDocumentProjectNameAndDocumentName(projectName, documentName).stream().filter(resource -> Arrays.asList(mimeTypes).contains(resource.getMimeType())).collect(Collectors.toList());
    }

    @Override
    protected String getChannel() {
        return "/channel/resource";
    }

}
