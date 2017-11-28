package edu.tamu.app.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.Role;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.auth.service.UserCredentialsService;

@Service
public class AppUserCredentialsService extends UserCredentialsService<AppUser, AppUserRepo> {

	@Override
	public synchronized AppUser updateUserByCredentials(Credentials credentials) {

		Optional<AppUser> user = userRepo.findByUsername(credentials.getUin());

		AppUser finalUser = null;

		// TODO: check to see if credentials is from basic login or shibboleth
		// do not create new user from basic login credentials that have no
		// user!
		if (!user.isPresent()) {

			Role role = Role.ROLE_USER;

			if (credentials.getRole() == null) {
				credentials.setRole(role.toString());
			}

			String shibEmail = credentials.getEmail();

			for (String email : admins) {
				if (email.equals(shibEmail)) {
					role = Role.ROLE_ADMIN;
					credentials.setRole(role.toString());
				}
			}

			finalUser = userRepo.create(credentials.getUin());
			finalUser.setUsername(credentials.getEmail());
			finalUser = userRepo.save(finalUser);
		} else {

			// TODO: update only if user properties does not match current
			// credentials
			finalUser = user.get();
			finalUser.setUsername(credentials.getEmail());
			finalUser = userRepo.save(finalUser);
		}

		credentials.setRole(finalUser.getRole().toString());
		credentials.setUin(finalUser.getUsername());

		return finalUser;

	}

	@Override
	public String getAnonymousRole() {
		return Role.ROLE_ANONYMOUS.toString();
	}

}
