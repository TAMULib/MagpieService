package edu.tamu.app.model.repo;

import java.util.Set;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.custom.MetadataFieldLabelRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface MetadataFieldLabelRepo extends WeaverRepo<MetadataFieldLabel>, MetadataFieldLabelRepoCustom {

    public MetadataFieldLabel findByNameAndProfile(String name, FieldProfile profile);
    public Set<MetadataFieldLabel> findByProfileId(Long fieldProfileId);
}
