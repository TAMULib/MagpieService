package edu.tamu.app.model.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.custom.FieldProfileRepoCustom;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

@Repository
public interface FieldProfileRepo extends WeaverRepo<FieldProfile>, FieldProfileRepoCustom {

    public FieldProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);

    public FieldProfile findByProjectAndGloss(Project project, String gloss);

    public List<FieldProfile> findByProject(Project project);

}
