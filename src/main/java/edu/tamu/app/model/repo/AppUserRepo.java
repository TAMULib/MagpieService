/* 
 * UserRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.tamu.app.model.AppUser;

/**
 * User repository.
 * 
 * @author
 *
 */
@Repository
public interface AppUserRepo extends JpaRepository <AppUser, Long> {
	
	/**
	 * Retrieve user by UIN.
	 * 
	 * @param 		uin				Long
	 * 
	 * @return		UserImpl
	 * 
	 */
	public AppUser getUserByUin(Long uin);

}
