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

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectProfile;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.ProjectRepoCustom;

/**
 *
 * 
 * @author
 *
 */
public class ProjectRepoImpl implements ProjectRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectProfileRepo projectFieldProfileRepo;

    @Autowired
    private DocumentRepo documentRepo;

    @Value("${app.defaultRepoUrl}")
    private String defaultRepoUrl;

    @Value("${app.defaultRepoUIPath}")
    private String defaultRepoUIPath;

    @Override
    public Project create(Project project) {
        // TODO: just using one url for all projects - make it per-project
        project.setRepositoryUIUrlString(defaultRepoUrl + "/" + defaultRepoUIPath);
        return projectRepo.save(project);
    }

    @Override
    public Project create(String name) {
        Project project = projectRepo.findByName(name);
        if (project == null) {
            return create(new Project(name));
        }
        return project;
    }

    @Override
    @Transactional
    public void delete(Project project) {
        Set<ProjectProfile> profiles = project.getProfiles();
        if (profiles.size() > 0) {
            profiles.parallelStream().forEach(profile -> {
                profile.setProject(null);
                projectFieldProfileRepo.save(profile);
            });
            project.clearProfiles();
        }

        Set<Document> documents = project.getDocuments();
        if (documents.size() > 0) {
            documents.parallelStream().forEach(document -> {
                document.setProject(null);
                documentRepo.save(document);
            });
            project.clearDocuments();
        }

        entityManager.remove(entityManager.contains(project) ? project : entityManager.merge(project));
    }

    @Override
    public void deleteAll() {
        projectRepo.findAll().parallelStream().forEach(project -> {
            projectRepo.delete(project);
        });
    }

}
