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

import edu.tamu.app.model.InputType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectProfile;
import edu.tamu.app.model.repo.custom.ProjectProfileRepoCustom;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface ProjectProfileRepo extends JpaRepository<ProjectProfile, Long>, ProjectProfileRepoCustom {

    public ProjectProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);

    public ProjectProfile findByProjectAndGlossAndRepeatableAndReadOnlyAndHiddenAndRequiredAndInputTypeAndDefaultValue(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);

    public List<ProjectProfile> findByProject(Project project);

    @Override
    public void delete(ProjectProfile profile);

    @Override
    public void deleteAll();

}
