/* 
 * ProjectRepo.java 
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

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {
	
	public Project create(Project project);
	
	public Project create(String name);
	
	public void delete(Project project);
	
	public void deleteAll();
	
	public Project findByName(String name);

}
