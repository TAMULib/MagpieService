/* 
 * CustomProjectRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import java.util.Set;

import edu.tamu.app.model.Project;

/**
 * 
 * 
 * @author
 *
 */
public interface ProjectRepoCustom {

	public Project create(String name);
    
    public Project create(String name, Set<String> authorities);

    public void delete(Project project);

    public void deleteAll();

}
