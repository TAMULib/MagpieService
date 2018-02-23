package edu.tamu.app.model.repo.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.model.repo.custom.AppUserRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class AppUserRepoImpl extends AbstractWeaverRepoImpl<AppUser, AppUserRepo> implements AppUserRepoCustom {

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
    public synchronized AppUser create(String uin) {
        Optional<AppUser> user = userRepo.findByUsername(uin);
        return user.isPresent() ? user.get() : userRepo.save(new AppUser(uin));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized AppUser create(String uin, String firstName, String lastName, String role) {
        Optional<AppUser> user = userRepo.findByUsername(uin);
        return user.isPresent() ? user.get() : userRepo.save(new AppUser(uin, firstName, lastName, role));
    }

    @Override
    protected String getChannel() {
        return "/channel/user";
    }

}