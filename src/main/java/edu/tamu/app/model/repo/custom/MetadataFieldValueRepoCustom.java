package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.ControlledVocabulary;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;

public interface MetadataFieldValueRepoCustom {

    public MetadataFieldValue create(ControlledVocabulary cv, MetadataFieldGroup field);

    public MetadataFieldValue create(String value, MetadataFieldGroup field);

    public MetadataFieldValue create(String value, MetadataFieldGroup field, ControlledVocabulary cv);

}
