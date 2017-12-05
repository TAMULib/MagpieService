package edu.tamu.app.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import edu.tamu.app.auth.model.AppUserDetails;
import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.weaver.auth.service.AbstractWeaverUserDetailsService;

@Service
public class AppUserDetailsService extends AbstractWeaverUserDetailsService<AppUser, AppUserRepo> {

    @Override
    public UserDetails buildUserDetails(AppUser user) {
        return new AppUserDetails(user);
    }

}
