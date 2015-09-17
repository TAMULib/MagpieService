/* 
 * StompInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import edu.tamu.framework.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.Credentials;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;

/**
 * Stomp interceptor. Checks command, decodes and verifies token, 
 * either returns error message to frontend or continues to controller.
 * 
 * @author 
 *
 */
@Component
public class AppStompInterceptor extends CoreStompInterceptor {
	
	@Autowired
	private AppUserRepo userRepo;
	
	@Value("${app.authority.admins}")
	private String[] admins;
	
	@Value("${app.authority.managers}")
	private String[] managers;

	/**
	 * @param       shib        Credentials
	 * 
	 * @return      shib
	 * 
	 * @see edu.tamu.framework.interceptor.CoreStompInterceptor#confirmCreateUser(edu.tamu.framework.model.Credentials)
	 */
	public Credentials confirmCreateUser(Credentials shib) {
		
		AppUser user = userRepo.getUserByUin(Long.parseLong(shib.getUin()));
		
		if(user == null) {
    		
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
    		
    		AppUser newUser = new AppUser();
    		
    		newUser.setUin(Long.parseLong(shib.getUin()));					
    		newUser.setRole(shib.getRole());
    		
    		newUser.setFirstName(shib.getFirstName());
    		newUser.setLastName(shib.getLastName());
    		
    		userRepo.save(newUser);
        	
        	//System.out.println(shib.getFirstName() + " " + shib.getLastName() + " connected with session id " + headers.get("simpSessionId"));
    		
    		System.out.println(Long.parseLong(shib.getUin()));	
    
    	}
    	else {
    		shib.setRole(user.getRole());
    	}
		
		return shib;
		
	}
	
	
}
