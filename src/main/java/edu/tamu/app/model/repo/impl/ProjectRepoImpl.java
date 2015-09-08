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

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldLabelRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.app.model.repo.MetadataFieldValueRepo;
import edu.tamu.app.model.repo.ProjectFieldProfileRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.model.repo.custom.CustomProjectRepo;

/** 
 * 
 * 
 * @author
 *
 */
public class ProjectRepoImpl implements CustomProjectRepo {
	
	@Autowired
	private ProjectRepo projectRepo;

	@Autowired
	private DocumentRepo documentRepo;
	
	@Autowired
	private MetadataFieldRepo metadataFieldRepo;
	
	@Autowired
	private MetadataFieldLabelRepo metadataFieldLabelRepo;
	
	@Autowired
	private MetadataFieldValueRepo metadataFieldValueRepo;
	
	@Autowired
	private ProjectFieldProfileRepo projectFieldProfileRepo;

	@Override
	public Project create(Project project) {
		
		System.out.println("SAVING PROJECT!!");
		
		projectRepo.save(project);
				
		return project;
		
	}

}