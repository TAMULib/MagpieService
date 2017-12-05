/* 
 * AppUserRepo.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.custom.AppUserRepoCustom;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;

/**
 * User repository.
 * 
 * @author
 *
 */
@Repository
public interface AppUserRepo extends AbstractWeaverUserRepo<AppUser>, AppUserRepoCustom {

}
