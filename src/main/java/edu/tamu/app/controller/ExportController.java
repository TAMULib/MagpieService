/* 
 * UserController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.MetadataLabel;
import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.impl.MetadataFieldImpl;
import edu.tamu.app.model.impl.MetadataLabelImpl;
import edu.tamu.app.model.repo.DocumentRepo;
import edu.tamu.app.model.repo.MetadataFieldRepo;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;

/** 
 * User Controller
 * 
 * @author
 *
 */
@RestController
@MessageMapping("/export")
public class ExportController {
	
	@Autowired
	DocumentRepo docRepo;
	
	@Autowired
	private MetadataFieldRepo metadataRepo;
	
	/**
	 * Websocket endpoint to request credentials.
	 * 
	 * @param 		shibObj			@Shib Object
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/saf")
	@RequestMapping("/saf")
	@SendToUser
	@Auth
	public ApiResponse saf(@Shib Object shibObj, @Data String data, @ReqId String requestId) throws Exception {
		
		System.out.println("Generating SAF for project " + data);
		
		//for each published document
		List<DocumentImpl> documents = docRepo.findByStatusAndProject("Published", data);
		
		
		//TODO:  get straight on where we want to write this bad boy
		
		String archiveDirectoryName = "exports/" + data+System.currentTimeMillis();
		if(documents.size() > 0)
		{
			//make a containing directory for the SAF
			
			File safDirectory = new File(archiveDirectoryName);
			safDirectory.mkdir();
		}
		
		for(DocumentImpl document: documents)
		{
			
			System.out.println("Writing archive for document " + document.getName());
			
			//create a directory
			File itemDirectory = new File(archiveDirectoryName + "/" + document.getName());
			itemDirectory.mkdir();
			
			//copy the content files to the directory
			File pdf = document.pdf();
			File txt = document.txt();
 			
 			try {
			    FileUtils.copyDirectory(pdf.getParentFile(), itemDirectory);
			} catch (IOException e) {
			    e.printStackTrace();
			}
 			
 			PrintStream license = new PrintStream(itemDirectory+"/license.txt");
 			license.print("The materials in this collection are copyrighted and have been digitally republished in the Texas A&M Digital Repository with the express permission of the copyright owner. The letter of permission is on file with the University Libraries.");
 			license.flush();
 			license.close();
 			
 			PrintStream manifest = new PrintStream(itemDirectory+"/content");
 			manifest.print(pdf.getName()+" "+txt.getName()+"     bundle:ORIGINAL primary:true     permissions:-r 'member'\nlicense.txt bundleLICENSE");
 			manifest.flush();
 			manifest.close();
 			
 			//for each schema in the metadata
				//write a dublin-core style xml file
 			
 			List<MetadataFieldImpl> metadataValueList = metadataRepo.getMetadataFieldsByName(document.getName());

 			PrintStream dublinCoreXml = new PrintStream(itemDirectory+"/dublin_core.xml");	
 			
 			dublinCoreXml.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dublin_core>");
 	
 			for(MetadataFieldImpl values : metadataValueList) {
 				String label = values.getLabel();
 				for(String value : values.getValues()) {
 					if(value.equals("")) continue;			
 					dublinCoreXml.print("<dcvalue element=\""+label+"\">"+value+"</dcvalue>");
 				}
 			}
 			dublinCoreXml.print("</dublin_core>");
 			dublinCoreXml.close();

		}
		return new ApiResponse("success", "Your SAF has been written to the server filesystem.", new RequestId(requestId));
	}
	

}
