package edu.tamu.app.model.repo.custom;

import java.util.List;

import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectSetting;
import edu.tamu.app.model.ServiceType;

public interface ProjectAuthorityRepoCustom {

    public ProjectAuthority create(String name, ServiceType serviceType);

    public ProjectAuthority create(String name, ServiceType serviceType, List<ProjectSetting> settings);

}
