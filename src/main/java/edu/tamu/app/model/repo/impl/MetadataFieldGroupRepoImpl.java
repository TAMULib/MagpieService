package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.MetadataFieldGroupRepo;
import edu.tamu.app.model.repo.custom.MetadataFieldGroupRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class MetadataFieldGroupRepoImpl extends AbstractWeaverRepoImpl<MetadataFieldGroup, MetadataFieldGroupRepo> implements MetadataFieldGroupRepoCustom {

    @Autowired
    private MetadataFieldGroupRepo metadataFieldGroupRepo;

    @Override
    public synchronized MetadataFieldGroup create(Document document, MetadataFieldLabel label) {
        MetadataFieldGroup metadataFieldGroup = metadataFieldGroupRepo.findByDocumentAndLabel(document, label);
        if (metadataFieldGroup == null) {
            metadataFieldGroup = metadataFieldGroupRepo.save(new MetadataFieldGroup(document, label));
        }
        return metadataFieldGroup;
    }

    @Override
    protected String getChannel() {
        return "/channel/metadata-field-group";
    }

}
