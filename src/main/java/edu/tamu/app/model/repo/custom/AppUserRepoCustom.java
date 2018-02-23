package edu.tamu.app.model.repo.custom;

import edu.tamu.app.model.AppUser;

public interface AppUserRepoCustom {

    /**
     * Creates application user based on uin in the repository
     * 
     * @param uin
     *            String
     */
    public AppUser create(String uin);

    public AppUser create(String uin, String firstName, String lastName, String role);

}
