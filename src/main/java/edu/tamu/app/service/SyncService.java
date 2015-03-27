/* 
 * SyncService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.repo.DocumentRepo;

/** 
 * Sync Service.
 * 
 * @author
 *
 */
@Service
@Component
public class SyncService {
	
	@Value("${app.directory}") 
	private String directory;
	
	@Autowired
	private DocumentRepo docRepo;
	
	@Scheduled(fixedDelay=60000)
	public void syncDocuments() {
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if(docRepo.getDocumentByFilename(listOfFiles[i].getName()) == null) {					
					DocumentImpl doc = new DocumentImpl(listOfFiles[i].getName(), "Unassigned");
					docRepo.save(doc);
				}
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}		
	}
}
