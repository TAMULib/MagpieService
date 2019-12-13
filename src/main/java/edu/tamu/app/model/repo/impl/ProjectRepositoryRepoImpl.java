package edu.tamu.app.model.repo.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectSetting;
import edu.tamu.app.model.ServiceType;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.ProjectRepositoryRepo;
import edu.tamu.app.model.repo.custom.ProjectRepositoryRepoCustom;
import edu.tamu.app.service.PropertyProtectionService;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProjectRepositoryRepoImpl extends AbstractWeaverRepoImpl<ProjectRepository, ProjectRepositoryRepo> implements ProjectRepositoryRepoCustom {

    @Autowired
    private ProjectRepositoryRepo projectRepositoryRepo;

    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    PropertyProtectionService propertyProtectionService;

    @Override
    public ProjectRepository create(ProjectRepository projectRepository) {
        return super.create(processProjectRepository(projectRepository));
    }

    @Override
    public ProjectRepository create(String name, ServiceType serviceType) {
        return create(name, serviceType, null);
    }

    @Override
    public ProjectRepository create(String name, ServiceType serviceType, List<ProjectSetting> settings) {
        ProjectRepository projectRepository = new ProjectRepository();
        projectRepository.setName(name);
        projectRepository.setType(serviceType);
        projectRepository.setSettings(settings);
        return this.create(projectRepository);
    }

    @Override
    public ProjectRepository update(ProjectRepository projectRepository) {
        ProjectRepository currentProjectRepository = projectRepositoryRepo.findOne(projectRepository.getId());
        for (int i=0;i<projectRepository.getSettings().size();i++) {
            ProjectSetting setting = projectRepository.getSettings().get(i);
            if (setting.isProtect() && setting.getValues().stream().allMatch(v -> v.equals(""))) {
                try {
                    setting.setValues(propertyProtectionService.decryptPropertyValues(currentProjectRepository.getSettingValues(setting.getKey())));
                    projectRepository.getSettings().set(i, setting);
                } catch (GeneralSecurityException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.update(processProjectRepository(projectRepository));
    }

    @Override
    public void delete(ProjectRepository projectRepository) {
        for (Project project:projectRepository.getProjects()) {
            project.removeRepository(projectRepository);
            projectRepo.update(project);
        }

        projectRepository.setProjects(new ArrayList<Project>());
        projectRepository = projectRepositoryRepo.update(projectRepository);
        super.delete(projectRepository);
    }

    @Override
    protected String getChannel() {
        return "/channel/project-repository";
    }

    private ProjectRepository processProjectRepository(ProjectRepository projectRepository) {
        projectRepository.setPropertyProtectionService(propertyProtectionService);
        projectRepository.getSettings().forEach(s -> {
            if (s.isProtect()) {
                try {
                    s.setValues(propertyProtectionService.encryptPropertyValues(s.getValues()));
                } catch (GeneralSecurityException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return projectRepository;
    }
}
