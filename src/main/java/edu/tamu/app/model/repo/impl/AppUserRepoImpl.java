/* 
 * UserRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.model.repo.custom.CustomAppUserRepo;

/** 
 * Implementaiton of the user repository.
 * 
 * @author
 *
 */
public class AppUserRepoImpl implements CustomAppUserRepo {

	@Autowired
	private AppUserRepo userRepo;
	
	/**
	 * Creates application user in the user repository
	 * 
	 * @param       uin        Long
	 * 
	 * @see edu.tamu.app.model.repo.custom.CustomAppUserRepo#create(java.lang.Long)
	 */
	@Override
	public AppUser create(Long uin) {
		AppUser user = null;
		if(userRepo.getUserByUin(uin)==null) {
			user = new AppUser(uin);
			userRepo.save(user);
		}
		return user;
	}

}