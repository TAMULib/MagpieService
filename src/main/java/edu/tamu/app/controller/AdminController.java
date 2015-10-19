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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import edu.tamu.app.service.SyncService;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

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
	
	private static final Logger logger = Logger.getLogger(AdminController.class);

	/**
	 * Synchronizes the project directory with the database.
	 * 
	 * @param 		message			Message<?>
	 * 
	 * @return		ApiResponse
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/sync")
	@Auth(role="ROLE_ADMIN")
	@SendToUser
	public ApiResponse syncDocuments(Message<?> message) throws Exception {
		
		logger.info("Syncronizing projects with database.");
		
		executorService.submit(syncService);
		
		return new ApiResponse(SUCCESS);
	}
	
}
