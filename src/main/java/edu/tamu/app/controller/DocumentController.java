/* 
 * DocumentController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.impl.DocumentImpl;
import edu.tamu.app.model.Document;
import edu.tamu.app.model.RequestId;

/** 
 * Document Controller
 * 
 * @author
 *
 */
@Component
@RestController
@MessageMapping("/document")
public class DocumentController {

	@Value("${app.directory}") 
	private String directory;
	
	@MessageMapping("/all")
	@SendToUser
	public ApiResImpl allDocuments(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		
		System.out.println(directory);
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		
		List<Document> docs = new ArrayList<Document>();
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				Document doc = new DocumentImpl(Long.parseLong(String.valueOf(i)), listOfFiles[i].getName(), "Unassigned");				 
				docs.add(doc);				
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}   
		    
		return new ApiResImpl("success", docs, new RequestId(requestId));
	}
	
	@MessageMapping("/{filename}")
	@SendToUser
	public ApiResImpl documentByFilename(@PathVariable("filename") String homeDBpatronId, Message<?> message) throws Exception {
		
		return null;
	}
	
}
