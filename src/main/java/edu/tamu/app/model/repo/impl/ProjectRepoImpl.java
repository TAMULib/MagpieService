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
import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.FieldProfileRepo;
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
    private FieldProfileRepo fieldProfileRepo;

    @Autowired
    private DocumentRepo documentRepo;

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
    
    @Override
    public synchronized Project create(String name, Set<String> authorities) {
        Project project = create(name);
        project.setAuthorities(authorities);
        return projectRepo.save(project);
    }

    @Override
    @Transactional
    public void delete(Project project) {
        Set<FieldProfile> profiles = project.getProfiles();
        if (profiles.size() > 0) {
            profiles.parallelStream().forEach(profile -> {
                profile.setProject(null);
                fieldProfileRepo.save(profile);
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
