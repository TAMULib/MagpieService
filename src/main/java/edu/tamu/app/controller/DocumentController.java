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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.impl.ApiResImpl;
import edu.tamu.app.model.repo.DocumentRepo;
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
	
	@Autowired
	private DocumentRepo docRepo;
	
	@MessageMapping("/all")
	@SendToUser
	public ApiResImpl allDocuments(Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		return new ApiResImpl("success", docRepo.findAll(), new RequestId(requestId));
	}
	
	@MessageMapping("/{filename}")
	@SendToUser
	public ApiResImpl documentByFilename(@PathVariable("filename") String filename, Message<?> message) throws Exception {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String requestId = accessor.getNativeHeader("id").get(0);
		return new ApiResImpl("success", docRepo.getDocumentByFilename(filename), new RequestId(requestId));
	}
	
}
