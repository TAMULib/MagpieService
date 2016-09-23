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
import edu.tamu.app.model.MetadataFieldLabel;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectProfile;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.ProjectProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.ProjectProfileRepoCustom;

/**
 *
 * 
 * @author
 *
 */
public class ProjectProfileRepoImpl implements ProjectProfileRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProjectProfileRepo projectProfileRepo;

    @Autowired
    private MetadataFieldLabelRepo metadataFieldLabelRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Override
    public synchronized ProjectProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue) {
        ProjectProfile profile = projectProfileRepo.findByProjectAndGlossAndRepeatableAndReadOnlyAndHiddenAndRequiredAndInputTypeAndDefaultValue(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue);
        if (profile == null) {
            return projectProfileRepo.save(new ProjectProfile(project, gloss, isRepeatable, isReadOnly, isHidden, isRequired, inputType, defaultValue));
        }
        return profile;
    }

    @Override
    @Transactional
    public void delete(ProjectProfile profile) {
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
        projectProfileRepo.findAll().parallelStream().forEach(profile -> {
            projectProfileRepo.delete(profile);
        });
    }

}
