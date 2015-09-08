/* 
 * UserRepoCustom.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.AppUser;

/**
 * Custom user repository interface.
 * 
 * @author
 *
 */
public interface AppUserRepoCustom {

	/**
	 * Creates application user based on uin in the repository
	 * 
	 * @param       uin         Long
	 */
	public AppUser create(Long uin);
	
}
