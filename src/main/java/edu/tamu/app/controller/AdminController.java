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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.RequestId;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
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
	
	@Value("${app.authority.admins}")
	private String[] admins;
	
	@Value("${app.authority.managers}")
	private String[] managers;
	
	@Autowired
	private AppUserRepo userRepo;
	
	@Autowired
	public ObjectMapper objectMapper;
	
	@Autowired 
    private ExecutorService executorService;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 
	
	/**
	 * Checks if user is in the repo. If not saves user to repo.
	 * 
	 * @param 		message			Message<?>
	 * @param 		shibObj			@Shib Object
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/confirmuser")
	@Auth(role="ROLE_ADMIN")
	@SendToUser
	public ApiResponse confirmUser(Message<?> message, @Shib Object shibObj, @ReqId String requestId) throws Exception {

		Credentials shib = (Credentials) shibObj;
		
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("changedUserUin", shib.getUin());
		
		if(userRepo.getUserByUin(Long.parseLong(shib.getUin())) == null) {
    		
    		AppUser newUser = new AppUser();
    		
    		if(shib.getRole() == null) {
    			shib.setRole("ROLE_USER");
    		}
    		
        	String shibUin = shib.getUin();
        	
        	for(String uin : managers) {
    			if(uin.equals(shibUin)) {
    				shib.setRole("ROLE_MANAGER");
    			}
    		}
        	
    		for(String uin : admins) {
    			if(uin.equals(shibUin)) {
    				shib.setRole("ROLE_ADMIN");
    			}
    		}
    		
    		newUser.setUin(Long.parseLong(shib.getUin()));
			newUser.setFirstName(shib.getFirstName());
			newUser.setLastName(shib.getLastName());
			newUser.setRole(shib.getRole());
			
			userRepo.save(newUser);
			
			userMap.put("list", userRepo.findAll());
			
			this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse("success", userMap, new RequestId(requestId)));
			
			return new ApiResponse("success", userMap, new RequestId(requestId));
		}
		
		userMap.put("list", userRepo.findAll());
				
		return new ApiResponse("success", userMap, new RequestId(requestId));
	}
	
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
		
		executorService.submit(new SyncService());
		
		return new ApiResponse("success", "ok", new RequestId(requestId));
	}
	
}
