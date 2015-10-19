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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectMinimal;
import edu.tamu.app.model.repo.custom.ProjectRepoCustom;

/**
 * 
 * 
 * @author
 *
 */
@Repository
public interface ProjectRepo extends JpaRepository <Project, Long>, ProjectRepoCustom {
	
	public Project create(Project project);
	
	public Project create(String name);

	public Project findByName(String name);
	
	/**
	 * Retrieve all projects, with fields from the primary table.
	 * 
	 * @return		List of Object
	 * 
	 */
	@Query(value = "SELECT new edu.tamu.app.model.ProjectMinimal(p.name, p.isLocked) FROM Project p ORDER BY p.name")
	public List<ProjectMinimal> findAllAsObject();
	
	@Override
	public void delete(Project project);
	
	@Override
	public void deleteAll();
	
}
