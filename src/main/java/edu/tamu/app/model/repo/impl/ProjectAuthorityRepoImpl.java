package edu.tamu.app.model.repo.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectSetting;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProjectAuthorityRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.ProjectAuthorityRepoCustom;
import edu.tamu.app.service.PropertyProtectionService;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProjectAuthorityRepoImpl extends AbstractWeaverRepoImpl<ProjectAuthority, ProjectAuthorityRepo> implements ProjectAuthorityRepoCustom {

    @Autowired
    private ProjectAuthorityRepo projectAuthorityRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    PropertyProtectionService propertyProtectionService;

    @Override
    public ProjectAuthority create(ProjectAuthority projectAuthority) {
        projectAuthority.setPropertyProtectionService(propertyProtectionService);
        projectAuthority.getSettings().forEach(s -> {
            if (s.isProtect()) {
                List<String> protectedValues;
                try {
                    protectedValues = propertyProtectionService.encryptPropertyValues(s.getValues());
                    s.setValues(protectedValues);
                } catch (GeneralSecurityException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return super.create(projectAuthority);
    }

    @Override
    public synchronized ProjectAuthority create(String name, ServiceType serviceType) {
        return create(name, serviceType, null);
    }

    @Override
    public synchronized ProjectAuthority create(String name, ServiceType serviceType, List<ProjectSetting> settings) {
        ProjectAuthority projectAuthority = new ProjectAuthority();
        projectAuthority.setName(name);
        projectAuthority.setType(serviceType);
        projectAuthority.setSettings(settings);
        return projectAuthorityRepo.create(projectAuthority);
    }

    @Override
    public void delete(ProjectAuthority projectAuthority) {
        for (Project project:projectAuthority.getProjects()) {
            project.removeAuthority(projectAuthority);
            projectRepo.update(project);
        }

        projectAuthority.setProjects(new ArrayList<Project>());
        projectAuthority = projectAuthorityRepo.update(projectAuthority);
        super.delete(projectAuthority);
    }

    @Override
    protected String getChannel() {
        return "/channel/project-authority";
    }
}
