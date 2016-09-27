/* 
 * ProjectFieldProfileRepo.java 
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

import edu.tamu.app.enums.InputType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.repo.custom.FieldProfileRepoCustom;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface FieldProfileRepo extends JpaRepository<FieldProfile, Long>, FieldProfileRepoCustom {

    public FieldProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);

    public FieldProfile findByProjectAndGloss(Project project, String gloss);

    public List<FieldProfile> findByProject(Project project);

    @Override
    public void delete(FieldProfile profile);

    @Override
    public void deleteAll();

}