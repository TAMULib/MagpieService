package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.InputType;
import edu.tamu.app.model.Project;

public interface FieldProfileRepoCustom {

    public FieldProfile create(Project project, String gloss, Boolean isRepeatable, Boolean isReadOnly, Boolean isHidden, Boolean isRequired, InputType inputType, String defaultValue);

}
