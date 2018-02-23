package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;

public interface MetadataFieldGroupRepoCustom {

    public MetadataFieldGroup create(Document document, MetadataFieldLabel label);

}
