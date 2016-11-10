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
import edu.tamu.app.model.repo.custom.AppUserRepoCustom;

/**
 * Implementaiton of the user repository.
 * 
 * @author
 *
 */
public class AppUserRepoImpl implements AppUserRepoCustom {

    @Autowired
    private AppUserRepo userRepo;

    /**
     * Creates application user in the user repository
     * 
     * @param uin
     *            Long
     * 
     * @see edu.tamu.app.model.repo.custom.AppUserRepoCustom#create(java.lang.Long)
     */
    @Override
    public synchronized AppUser create(Long uin) {
        AppUser user = userRepo.findByUin(uin);
        if (user == null) {
            user = userRepo.save(new AppUser(uin));
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized AppUser create(Long uin, String firstName, String lastName, String role) {
        AppUser user = userRepo.findByUin(uin);
        if (user == null) {
            user = userRepo.save(new AppUser(uin, firstName, lastName, role));
        }
        return user;
    }

}