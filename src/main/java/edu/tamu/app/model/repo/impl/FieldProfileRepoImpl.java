/* 
 * ProjectFieldProfileRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.FieldProfileRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.FieldProfileRepoCustom;

/**
 *
 * 
 * @author
 *
 */
public class FieldProfileRepoImpl implements FieldProfileRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Override
    public synchronized FieldProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue) {
        FieldProfile fieldProfile = fieldProfileRepo.findByProjectAndGloss(project, gloss);
        if (fieldProfile == null) {
            fieldProfile = fieldProfileRepo.save(new FieldProfile(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue));
        }
        return fieldProfile;
    }

    @Override
    @Transactional
    public void delete(FieldProfile profile) {

        Set<MetadataFieldLabel> labels = profile.getLabels();
        if (labels.size() > 0) {
            labels.parallelStream().forEach(l -> {
                l.setProfile(null);
                metadataFieldLabelRepo.save(l);
            });
            profile.clearLabels();
        }

        Project project = profile.getProject();
        if (project != null) {
            profile.setProject(null);
            project.removeProfile(profile);
            projectRepo.save(project);
        }

        entityManager.remove(entityManager.contains(profile) ? profile : entityManager.merge(profile));
    }

    @Override
    public void deleteAll() {
        fieldProfileRepo.findAll().parallelStream().forEach(profile -> {
            fieldProfileRepo.delete(profile);
        });
    }

}
