/* 
 * ProjectRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import java.util.List;

import edu.tamu.app.model.FieldProfile;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectSuggestor;

/**
 * 
 * 
 * @author
 *
 */
public interface ProjectRepoCustom {

    public Project create(String name);

    public Project create(String name, List<ProjectRepository> repositories, List<ProjectAuthority> authorities, List<ProjectSuggestor> suggestors);

}
