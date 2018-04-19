package edu.tamu.app.model.repo.custom;

import java.util.List;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Resource;

public interface ResourceRepoCustom {

    public Resource create(Document document, String name, String path, String mimeType);

    public List<Resource> findAllByDocumentProjectNameAndDocumentNameAndMimeType(String projectName, String documentName, String... mimeTypes);

}
