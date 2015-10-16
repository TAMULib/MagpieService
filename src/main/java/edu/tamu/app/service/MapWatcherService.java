/* 
 * WatcherService.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.service;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataFieldGroup;
import edu.tamu.app.model.MetadataFieldValue;
import edu.tamu.app.model.Project;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.ProjectRepo;
import edu.tamu.app.utilities.CsvUtility;


/** 
 * Watches map file folder, harvests contents, and updates app data as needed
 * 
 * @author
 *
 */
@Service
@Scope(value = "prototype")
public class MapWatcherService implements Runnable {
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private ProjectRepo projectRepo;
	
	@Autowired
	private DocumentRepo documentRepo;
	
	@Value("${app.host}") 
   	private String host;
	
	@Value("${app.mount}") 
   	private String mount;
	
	@Value("${app.symlink.create}") 
   	private String link;
	
	private String folder;
	
	private static final Logger logger = Logger.getLogger(MapWatcherService.class);
	
	/**
	 * Default constructor.
	 * 
	 */
	public MapWatcherService(){
		super();
	}
	
	public MapWatcherService(String folder) {
		super();
		this.folder = folder;
	}
	
	public String getFolder() {
		return folder;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	/**
	 * WatcherService runnable.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		String directory = "";
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/" + folder;
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = FileSystems.getDefault().getPath(directory, "");
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
             
            logger.info("MapWatch Service registered for dir: " + dir.getFileName());
            //the string representing the published state
            String changeStatus = "Published";
            while (true) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path file = ev.context();
                    
                    logger.info(kind.name() + ": " + file);
                    if (kind == ENTRY_CREATE) {
                    	String line;
                    	try {
                        	//read and iterate over mapfile
                    		String mapFileName = directory+"/"+file.toFile();
                    		if (logger.isDebugEnabled()) {
                    			logger.debug("The map file is named: "+mapFileName);
                    		}
                    		InputStream stream = new FileInputStream(mapFileName);
                    	    InputStreamReader sReader = new InputStreamReader(stream);
                    		BufferedReader bReader = new BufferedReader(sReader);
                    		//the project to unlock, if all documents have been published
                    		String unlockableProjectName = null;
            				logger.info("Reading mapfile: "+file);
                    		
                    		while ((line = bReader.readLine()) != null) {
                    			//extract document name from mapfile row
                    			String[] itemData = line.split(" ");
                    			String documentName = itemData[0];
                        		if (logger.isDebugEnabled()) {
                        			logger.debug("The document name is: "+documentName);
                        		}

                    			Document updateDoc = documentRepo.findByName(documentName);

                    			if (updateDoc != null) {
                    				if (unlockableProjectName == null) {
                    					unlockableProjectName = updateDoc.getProject().getName();
                    				}
                    				updateDoc.setStatus(changeStatus);
                    				documentRepo.save(updateDoc);
                    				logger.info("Setting status of Document: "+updateDoc.getName()+" to "+changeStatus);
                    			} else {
                    				logger.info("No Document found for string: "+documentName);
                    			}
                    		}
                			if (unlockableProjectName != null) {
                				List<Document> unpublishedDocs = documentRepo.findByProjectNameAndStatus(unlockableProjectName,"Pending");
                            	//unlock project if there are no pending documents
                				if (unpublishedDocs.size() == 0) {
                					//get the project fresh so the documents we modified above keep their changes
                					Project unlockableProject = projectRepo.findByName(unlockableProjectName);
                					unlockableProject.setIsLocked(false);
                					projectRepo.save(unlockableProject);
                					logger.info("Project '"+unlockableProject.getName()+"' unlocked.");
                					generateArchiveMaticaCSV(unlockableProject.getName());
                				} else {
                					logger.info("Project '"+unlockableProjectName+"' was left locked because there was a count of  "+unpublishedDocs.size()+" unpublished document(s).");
                				}
               				} else {
                				logger.info("No Project found");
                			}
                			bReader.close();
                			File mapFile = new File(mapFileName);
                			if (mapFile.delete()) {
                				logger.info("Mapfile: "+mapFileName+" removed.");
                			} else {
                				logger.info("Error removing mapfile: "+mapFileName+".");
                			}
                    	} catch (IOException e) {
                            logger.error(e);
                    	}
                    	
                    }
                }
                boolean valid = key.reset();
                
                if (!valid) {
                    break;
                }
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
	}
	
	private void generateArchiveMaticaCSV(String projectName) {
		logger.info("Writing Archivematica CSV for: "+projectName);
		String [] elements = {"title","creator", "subject","description", "publisher","contributor", "date","type", "format","identifier", "source", "language", "relation","coverage", "rights"};		
		String directory = "";
		try {
			directory = appContext.getResource("classpath:static" + mount).getFile().getAbsolutePath() + "/archivematica/";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String archiveDirectoryName = directory+projectName;
		
		List<Document> documents = documentRepo.findByProjectNameAndStatus(projectName, "Published");
		if(documents.size() > 0) {	
			File archiveDirectory = new File(archiveDirectoryName);
			if (archiveDirectory.isDirectory() == false) {
				archiveDirectory.mkdir();
			}
	
			Date date  = new Date();
			String formatDate = new SimpleDateFormat("YYYY/mm/dd").format(date);
			
			Map<String,String> map = new HashMap<String, String>();
			map.put("dc.identifier","");
			map.put("dc.source","");
			map.put("dc.relation","");
			map.put("dc.coverage","");
			for(Document document: documents) {		
				File itemDirectory = new File(archiveDirectoryName + "/" + document.getName());
				if (itemDirectory.isDirectory() == false) {
					itemDirectory.mkdir();
				}
				Set<MetadataFieldGroup> metadataFields = document.getFields(); 			
	 		
				metadataFields.forEach(field -> {
						String values ="";
						boolean firstPass = true;
						for(MetadataFieldValue medataFieldValue : field.getValues()) {					
							if(firstPass) {
								values = medataFieldValue.getValue();
								firstPass = false;
							}
							else {
								values += "||" +  medataFieldValue.getValue();
							}					
						}
						map.put(field.getLabel().getName(),values);
					});

				// writing to the ArchiveMatica format metadata csv file
				try{
					CsvUtility csvUtil = new CsvUtility();
					ArrayList<String> csvRow = new ArrayList<String>();
					csvRow.add("parts");
					for(int i=0;i<elements.length;i++) {
						//writing the element 
						for(Map.Entry<String, String> entry : map.entrySet()) {
							if(entry.getKey().contains(elements[i])) {						
								csvRow.add(entry.getKey());
							}
						}
					}
					csvUtil.appendRow(csvRow);
					csvRow.clear();
					csvRow.add("objects/"+document.getName());
					//writing the data values
					for(int i=0;i<elements.length;i++) {
						for(Map.Entry<String,String> entry : map.entrySet()) {
							if(entry.getKey().contains(elements[i])) {
								
								if(entry.getKey().contains("parts")) {
									map.put(entry.getKey(), "objects/"+document.getName());
								}
								if(entry.getKey().contains("date")) {
									map.put(entry.getKey(), formatDate);
								}
								if(entry.getKey().contains("type")) {
									map.put(entry.getKey(), "Archival Information Package");
								}
								if(entry.getKey().contains("format")) {
									map.put(entry.getKey(), "Image/tiff");
								}
								if(entry.getKey().contains("language")) {
									map.put(entry.getKey(), "English");
								}
								csvRow.add(entry.getValue());
							}
						}
					}
					csvUtil.appendRow(csvRow);
					csvRow.clear();
					csvUtil.generateCsvFile(itemDirectory+"/metadata_"+System.currentTimeMillis()+".csv");
				} catch(Exception ioe) {
					logger.error(ioe);
				}
			}
		}
	}
}
