/* 
 * AppControllerAspect.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.framework.aspect.CoreControllerAspect;

/** 
 * Application Controller Aspect
 * 
 * @author
 *
 */
@Component
@Aspect
public class AppControllerAspect extends CoreControllerAspect {

	@Autowired
	AppUserRepo userRepo;
	
	/**
	 * Returns user role from  user repository by uin
	 * 
	 * @param       uin             String
	 * 
	 * @see edu.tamu.framework.aspect.CoreControllerAspect#getUserRole(java.lang.String)
	 * 
	 */
	@Override
	public String getUserRole(String uin) {
		return userRepo.findOne(Long.parseLong(uin)).getRole();
	}
	
}
