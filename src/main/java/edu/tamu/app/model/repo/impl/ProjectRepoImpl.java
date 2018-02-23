package edu.tamu.app.model.repo.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.IngestType;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.ProjectRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class ProjectRepoImpl extends AbstractWeaverRepoImpl<Project, ProjectRepo> implements ProjectRepoCustom {

    @Autowired
    private ProjectRepo projectRepo;

    @Override
    public synchronized Project create(String name, IngestType ingestType, boolean headless) {
        Project project = projectRepo.findByName(name);
        if (project == null) {
            project = new Project(name, ingestType, headless);
        }
        return projectRepo.create(project);
    }

    @Override
    public synchronized Project create(String name, IngestType ingestType, boolean headless, List<ProjectRepository> repositories, List<ProjectAuthority> authorities, List<ProjectSuggestor> suggestors) {
        Project project = projectRepo.findByName(name);
        if (project == null) {
            project = new Project(name, ingestType, headless);
        }
        project.setRepositories(repositories);
        project.setAuthorities(authorities);
        project.setSuggestors(suggestors);
        return projectRepo.create(project);
    }

    @Override
    protected String getChannel() {
        return "/channel/project";
    }

}
