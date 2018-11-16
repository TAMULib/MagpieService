package edu.tamu.app.model.repo.impl;

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
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProjectRepositoryRepoImpl extends AbstractWeaverRepoImpl<ProjectRepository, ProjectRepositoryRepo> implements ProjectRepositoryRepoCustom {

    @Autowired
    private ProjectRepositoryRepo projectRepositoryRepo;

    @Autowired
    ProjectRepo projectRepo;

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
        return projectRepositoryRepo.create(projectRepository);
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
}
