/* 
 * UserController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.weaver.auth.annotation.WeaverCredentials;
import edu.tamu.weaver.auth.model.Credentials;
import edu.tamu.weaver.response.ApiResponse;

/**
 * User Controller
 * 
 * @author
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AppUserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Websocket endpoint to request credentials.
     * 
     * @param credentials
     * @ApiCredentials Credentials
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/credentials")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse credentials(@WeaverCredentials Credentials credentials) {
        return new ApiResponse(SUCCESS, credentials);
    }

    /**
     * Endpoint to return all users.
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    /**
     * Endpoint to update users role.
     * 
     * @param user
     * @ApiModel AppUser
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse updateRole(@RequestBody AppUser user) {
        user = userRepo.save(user);
        simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));
        return new ApiResponse(SUCCESS, user);
    }

    /**
     * Endpoint to delete user.
     * 
     * @param user
     * @ApiModel AppUser
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/delete")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse delete(@RequestBody AppUser user) throws Exception {
        userRepo.delete(user);
        simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

}
