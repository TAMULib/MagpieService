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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.ReqId;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;
import edu.tamu.framework.model.RequestId;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;

/** 
 * User Controller
 * 
 * @author
 *
 */
@RestController
@MessageMapping("/user")
public class UserController {

	@Autowired
	private AppUserRepo userRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate; 
	
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
	@MessageMapping("/credentials")
	@RequestMapping("/credentials")
	@SendToUser
	@Auth
	public ApiResponse credentials(@Shib Object shibObj, @ReqId String requestId) throws Exception {
		return new ApiResponse("success", (Credentials) shibObj, new RequestId(requestId));
	}
	
	
	/**
	 * Endpoint to return all users.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 *  
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/all")
	@Auth
	@SendToUser
	public ApiResponse allUsers(Message<?> message, @ReqId String requestId) throws Exception {
		Map<String,List<AppUser>> map = new HashMap<String,List<AppUser>>();
		map.put("list", userRepo.findAll());		
		return new ApiResponse("success", map, new RequestId(requestId));
	}
	
	/**
	 * Endpoint to update users role.
	 * 
	 * @param 		message			Message<?>
	 * @param 		requestId		@ReqId String
	 * 
	 * @return		ApiResImpl
	 * 
	 * @throws 		Exception
	 * 
	 */
	@MessageMapping("/update_role")
	@Auth
	@SendToUser
	public ApiResponse updateRole(Message<?> message, @ReqId String requestId) throws Exception {		
		
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		String data = accessor.getNativeHeader("data").get(0).toString();		

		Map<String,String> map = new HashMap<String,String>();		
		try {
			map = objectMapper.readValue(data, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}		
		AppUser user = userRepo.getUserByUin(Long.decode(map.get("uin")));		
		user.setRole(map.get("role"));		
		userRepo.save(user);
		
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("list", userRepo.findAll());
		userMap.put("changedUserUin", map.get("uin"));
		
		this.simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse("success", userMap, new RequestId(requestId)));
		
		return new ApiResponse("success", "ok", new RequestId(requestId));
	}

}
