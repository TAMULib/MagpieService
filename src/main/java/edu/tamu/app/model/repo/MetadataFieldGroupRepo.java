/* 
 * MetadataFieldRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.repo.custom.MetadataFieldGroupRepoCustom;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface MetadataFieldGroupRepo extends JpaRepository<MetadataFieldGroup, Long>, MetadataFieldGroupRepoCustom {

    public MetadataFieldGroup create(Document document, MetadataFieldLabel label);

    public List<MetadataFieldGroup> findByDocument(Document document);

    public List<MetadataFieldGroup> findByLabel(MetadataFieldLabel label);

    public MetadataFieldGroup findByDocumentAndLabel(Document document, MetadataFieldLabel label);

    @Override
    public void delete(MetadataFieldGroup field);

    @Override
    public void deleteAll();

}
