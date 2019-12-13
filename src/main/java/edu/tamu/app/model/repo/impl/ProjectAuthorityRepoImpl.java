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
        return super.create(processProjectAuthority(projectAuthority));
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
        return this.create(projectAuthority);
    }

    @Override
    public ProjectAuthority update(ProjectAuthority projectAuthority) {
        ProjectAuthority currentProjectAuthority = projectAuthorityRepo.findOne(projectAuthority.getId());
        for (int i=0;i<projectAuthority.getSettings().size();i++) {
            ProjectSetting setting = projectAuthority.getSettings().get(i);
            if (setting.isProtect() && setting.getValues().stream().allMatch(v -> v.equals(""))) {
                try {
                    setting.setValues(propertyProtectionService.decryptPropertyValues(currentProjectAuthority.getSettingValues(setting.getKey())));
                    projectAuthority.getSettings().set(i, setting);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.update(processProjectAuthority(projectAuthority));
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

    private ProjectAuthority processProjectAuthority(ProjectAuthority projectAuthority) {
        projectAuthority.setPropertyProtectionService(propertyProtectionService);
        projectAuthority.getSettings().forEach(s -> {
            if (s.isProtect()) {
                try {
                    s.setValues(propertyProtectionService.encryptPropertyValues(s.getValues()));
                } catch (GeneralSecurityException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return projectAuthority;
    }
}
