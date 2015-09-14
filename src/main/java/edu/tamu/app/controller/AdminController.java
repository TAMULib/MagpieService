/* 
 * AdminController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.RequestId;
import edu.tamu.app.service.SyncService;

/** 
 * Admin Controller.
 * 
 * @author
 *
 */
@RestController
@MessageMapping("/admin")
public class AdminController {
	
	@Autowired
	private SyncService syncService;
	
	@Autowired
	private ExecutorService executorService;

	/**
	 * Synchronizes the project directory with the database.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/sync")
	@Auth(role="ROLE_ADMIN")
	@SendToUser
	public ApiResponse syncDocuments(Message<?> message, @ReqId String requestId) throws Exception {
		
		System.out.println("Syncronizing projects with database.");
		
		executorService.submit(syncService);
		
		return new ApiResponse("success", "ok", new RequestId(requestId));
	}
	
}
