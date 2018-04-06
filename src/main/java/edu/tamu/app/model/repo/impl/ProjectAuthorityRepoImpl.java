package edu.tamu.app.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectSetting;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProjectAuthorityRepo;
import edu.tamu.app.model.repo.custom.ProjectAuthorityRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProjectAuthorityRepoImpl extends AbstractWeaverRepoImpl<ProjectAuthority, ProjectAuthorityRepo> implements ProjectAuthorityRepoCustom {

    @Autowired
    private ProjectAuthorityRepo projectAuthorityRepo;

    @Override
    public synchronized ProjectAuthority create(String name, ServiceType serviceType) {
        return create(name,serviceType,null);
    }

    @Override
    public synchronized ProjectAuthority create(String name, ServiceType serviceType, List<ProjectSetting> settings) {
        ProjectAuthority projectAuthority = projectAuthorityRepo.findByName(name);
        if (projectAuthority == null) {
            projectAuthority = new ProjectAuthority();
            projectAuthority.setName(name);
            projectAuthority.setType(serviceType);
        }
        projectAuthority.setSettings(settings);
        return projectAuthorityRepo.create(projectAuthority);
    }

    @Override
    protected String getChannel() {
        return "/channel/project-authority";
    }
}
