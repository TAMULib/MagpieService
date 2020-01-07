package edu.tamu.app.model.repo.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectSetting;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ProjectSuggestorRepo;
import edu.tamu.app.model.repo.custom.ProjectSuggestorRepoCustom;
import edu.tamu.app.service.PropertyProtectionService;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProjectSuggestorRepoImpl extends AbstractWeaverRepoImpl<ProjectSuggestor, ProjectSuggestorRepo> implements ProjectSuggestorRepoCustom {

    @Autowired
    private ProjectSuggestorRepo projectSuggestorRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    PropertyProtectionService propertyProtectionService;

    @Override
    public ProjectSuggestor create(ProjectSuggestor projectSuggestor) {
        return super.create(processProjectSuggestor(projectSuggestor));
    }

    @Override
    public ProjectSuggestor create(String name, ServiceType serviceType) {
        return create(name, serviceType, null);
    }

    @Override
    public ProjectSuggestor create(String name, ServiceType serviceType, List<ProjectSetting> settings) {
        ProjectSuggestor projectSuggestor = new ProjectSuggestor();
        projectSuggestor.setName(name);
        projectSuggestor.setType(serviceType);
        projectSuggestor.setSettings(settings);
        return this.create(projectSuggestor);
    }

    @Override
    public ProjectSuggestor update(ProjectSuggestor projectSuggestor) {
        ProjectSuggestor currentProjectSuggestor = projectSuggestorRepo.findOne(projectSuggestor.getId());
        for (int i=0;i<projectSuggestor.getSettings().size();i++) {
            ProjectSetting setting = projectSuggestor.getSettings().get(i);
            if (setting.isProtect() && setting.getValues().stream().allMatch(v -> v.equals(""))) {
                try {
                    setting.setValues(propertyProtectionService.decryptPropertyValues(currentProjectSuggestor.getSettingValues(setting.getKey())));
                    projectSuggestor.getSettings().set(i, setting);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.update(processProjectSuggestor(projectSuggestor));
    }

    @Override
    public void delete(ProjectSuggestor projectSuggestor) {
        for (Project project:projectSuggestor.getProjects()) {
            project.removeSuggestor(projectSuggestor);
            projectRepo.update(project);
        }

        projectSuggestor.setProjects(new ArrayList<Project>());
        projectSuggestor = projectSuggestorRepo.update(projectSuggestor);
        super.delete(projectSuggestor);
    }

    @Override
    protected String getChannel() {
        return "/channel/project-suggestor";
    }

    private ProjectSuggestor processProjectSuggestor(ProjectSuggestor projectSuggestor) {
        projectSuggestor.setPropertyProtectionService(propertyProtectionService);
        projectSuggestor.getSettings().forEach(s -> {
            if (s.isProtect()) {
                try {
                    s.setValues(propertyProtectionService.encryptPropertyValues(s.getValues()));
                } catch (GeneralSecurityException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return projectSuggestor;
    }
}
