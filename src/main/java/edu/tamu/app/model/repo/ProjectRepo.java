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
import edu.tamu.app.model.repo.custom.CustomProjectRepo;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface ProjectRepo extends JpaRepository <Project, Long>, CustomProjectRepo {
	
	public Project create(Project project);
	
	public Project create(String name);

	public Project findByName(String name);
	
	@Override
	public void delete(Project project);
	
	@Override
	public void deleteAll();
	
}
