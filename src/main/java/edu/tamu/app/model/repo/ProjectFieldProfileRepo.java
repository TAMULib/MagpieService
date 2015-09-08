/* 
 * ProjectFieldProfileRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectFieldProfile;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface ProjectFieldProfileRepo extends JpaRepository<ProjectFieldProfile, Long> {
	
	public ProjectFieldProfile findByProject(Project project);

}
