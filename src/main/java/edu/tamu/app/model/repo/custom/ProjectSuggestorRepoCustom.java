package edu.tamu.app.model.repo.custom;

import java.util.List;

import edu.tamu.app.model.ProjectSetting;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.ServiceType;

public interface ProjectSuggestorRepoCustom {

    public ProjectSuggestor create(String name, ServiceType serviceType);

    public ProjectSuggestor create(String name, ServiceType serviceType, List<ProjectSetting> settings);

}
