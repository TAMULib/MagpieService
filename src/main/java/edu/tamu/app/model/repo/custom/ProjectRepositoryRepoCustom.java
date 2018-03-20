package edu.tamu.app.model.repo.custom;

import java.util.List;

import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectSetting;
import edu.tamu.app.model.ServiceType;

public interface ProjectRepositoryRepoCustom {

    public ProjectRepository create(String name, ServiceType serviceType);

    public ProjectRepository create(String name, ServiceType serviceType, List<ProjectSetting> settings);

}
