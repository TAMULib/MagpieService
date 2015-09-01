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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 			license.print("The materials in this collection are hereby licensed.");
 			license.flush();
 			license.close();
 			
 			PrintStream manifest = new PrintStream(itemDirectory+"/contents");
 			manifest.print(pdf.getName()+"\tbundle:ORIGINAL\tprimary:true\tpermissions:-r 'member'\nlicense.txt\tbundle:LICENSE");
 			manifest.flush();
 			manifest.close();
 			
 			//for each schema in the metadata
 			Map <String, PrintStream> schemaToFile = new HashMap<String, PrintStream>();
 			List<MetadataFieldImpl> metadataValueList = metadataRepo.getMetadataFieldsByName(document.getName());
 			
 			for(MetadataFieldImpl values : metadataValueList) 
 			{
 	 			//write a dublin-core style xml file
 				String label = values.getLabel();
 				String schema = label.split("\\.")[0];
 				//System.out.println("Got schema " + schema);
 				String element = label.split("\\.")[1];
 				//System.out.println("Got element "+ element);
 				String qualifier = null;
 				if(label.split("\\.").length > 2)
 					qualifier = label.split("\\.")[2];
 					
 				
 				if(!schemaToFile.containsKey(schema))
 				{
 					String filename = schema.equals("dc") ? "dublin_core.xml" : "metadata_"+schema+".xml";
 					schemaToFile.put(schema, new PrintStream(itemDirectory+"/"+filename));
 					schemaToFile.get(schema).print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><dublin_core schema=\""+ schema + "\">"); 	 				
 				}
 				
 				
 				for(String value : values.getValues()) {
 					if(value.equals("")) continue;
 					
 					value = escapeForXML(value);
 					
 					schemaToFile.get(schema).print("<dcvalue element=\""+element+"\" "
 							+ ( qualifier!=null? "qualifier=\"" + qualifier + "\"" : "" ) +
 							">"+value+"</dcvalue>");
 				}
 			}
 			
 			for(PrintStream printStream : schemaToFile.values())
			{
				printStream.print("</dublin_core>");
				printStream.close();
			}
		

		}
		return new ApiResponse("success", "Your SAF has been written to the server filesystem.", new RequestId(requestId));
	}

	private String escapeForXML(String value) {
		value = value.replace("&", "&amp;");
		value = value.replace("\"", "&quot;");
		value = value.replace("'", "&apos;");
		value = value.replace("<", "&lt;");
		value = value.replace(">", "&gt;");
		return value;
	}
	

}
