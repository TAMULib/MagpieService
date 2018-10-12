package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.custom.MetadataFieldValueRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class MetadataFieldValueRepoImpl extends AbstractWeaverRepoImpl<MetadataFieldValue, MetadataFieldValueRepo> implements MetadataFieldValueRepoCustom {

    @Autowired
    private MetadataFieldValueRepo metadataFieldValueRepo;

    @Override
    public MetadataFieldValue create(ControlledVocabulary cv, MetadataFieldGroup field) {
        return metadataFieldValueRepo.save(new MetadataFieldValue(cv, field));
    }

    @Override
    public MetadataFieldValue create(String value, MetadataFieldGroup field) {
        return metadataFieldValueRepo.save(new MetadataFieldValue(value, field));
    }

    @Override
    public MetadataFieldValue create(String value, MetadataFieldGroup field, ControlledVocabulary cv) {
        return metadataFieldValueRepo.save(new MetadataFieldValue(value, cv, field));
    }

    @Override
    protected String getChannel() {
        return "/channel/metadata-field-value";
    }

}
