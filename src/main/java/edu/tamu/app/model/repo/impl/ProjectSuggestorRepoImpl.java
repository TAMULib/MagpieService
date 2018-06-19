package edu.tamu.app.model.repo.impl;

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
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProjectSuggestorRepoImpl extends AbstractWeaverRepoImpl<ProjectSuggestor, ProjectSuggestorRepo> implements ProjectSuggestorRepoCustom {

    @Autowired
    private ProjectSuggestorRepo projectSuggestorRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Override
    public synchronized ProjectSuggestor create(String name, ServiceType serviceType) {
        return create(name,serviceType,null);
    }

    @Override
    public synchronized ProjectSuggestor create(String name, ServiceType serviceType, List<ProjectSetting> settings) {
        ProjectSuggestor projectSuggestor = projectSuggestorRepo.findByName(name);
        if (projectSuggestor == null) {
            projectSuggestor = new ProjectSuggestor();
            projectSuggestor.setName(name);
            projectSuggestor.setType(serviceType);
        }
        projectSuggestor.setSettings(settings);
        return projectSuggestorRepo.create(projectSuggestor);
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
}
