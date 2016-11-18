/* 
 * ProjectRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.ProjectRepoCustom;

/**
 *
 * 
 * @author
 *
 */
public class ProjectRepoImpl implements ProjectRepoCustom {

    @Autowired
    private ProjectRepo projectRepo;

    @Value("${app.defaultRepoUrl}")
    private String defaultRepoUrl;

    @Value("${app.defaultRepoUIPath}")
    private String defaultRepoUIPath;

    @Override
    public synchronized Project create(String name) {
        Project project = projectRepo.findByName(name);
        if (project == null) {
            project = new Project(name);
        }
        project.setRepositoryUIUrlString(defaultRepoUrl + "/" + defaultRepoUIPath);
        return projectRepo.save(project);
    }

}
