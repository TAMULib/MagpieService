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
import edu.tamu.app.model.repo.custom.AppUserRepoCustom;

/**
 * User repository.
 * 
 * @author
 *
 */
@Repository
public interface AppUserRepo extends JpaRepository <AppUser, Long>, AppUserRepoCustom{
	
	/**
	 * Retrieve user by UIN.
	 * 
	 * @param 		uin				Long
	 * 
	 * @return		UserImpl
	 * 
	 */
	public AppUser findByUin(Long uin);

}
