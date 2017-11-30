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

		if (!user.isPresent()) {

			Role role = Role.ROLE_USER;

			if (credentials.getRole() == null) {
				credentials.setRole(role.toString());
			}

			String shibEmail = credentials.getEmail();
			String shibUin = credentials.getUin();

			for (String uin : admins) {
				if (uin.equals(shibUin)) {
					role = Role.ROLE_ADMIN;
					credentials.setRole(role.toString());
				}
			}

			finalUser = userRepo.create(credentials.getUin());
			finalUser.setUsername(credentials.getUin());
			finalUser.setRole(role);
			finalUser = userRepo.save(finalUser);

		} else {
			finalUser = user.get();
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
