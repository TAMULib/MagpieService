package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.custom.MetadataFieldLabelRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class MetadataFieldLabelRepoImpl extends AbstractWeaverRepoImpl<MetadataFieldLabel, MetadataFieldLabelRepo> implements MetadataFieldLabelRepoCustom {

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Override
    public MetadataFieldLabel create(String name, FieldProfile profile) {
        return metadataFieldLabelRepo.save(new MetadataFieldLabel(name, profile));
    }

    @Override
    protected String getChannel() {
        return "/channel/metadata-field-label";
    }

}
