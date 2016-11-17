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

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.framework.aspect.annotation.ApiCredentials;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.ApiModel;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

/**
 * User Controller
 * 
 * @author
 *
 */
@RestController
@ApiMapping("/user")
public class UserController {

    @Autowired
    private AppUserRepo userRepo;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Websocket endpoint to request credentials.
     * 
     * @param credentials
     *          @ApiCredentials Credentials
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/credentials")
    @Auth(role = "ROLE_USER")
    public ApiResponse credentials(@ApiCredentials Credentials credentials) {
        return new ApiResponse(SUCCESS, credentials);
    }

    /**
     * Endpoint to return all users.
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_USER")
    public ApiResponse allUsers() {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    /**
     * Endpoint to update users role.
     * 
     * @param user
     *          @ApiModel AppUser
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/update")
    @Auth(role = "ROLE_USER")
    public ApiResponse updateRole(@ApiModel AppUser user) {
        user = userRepo.save(user);
        simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));
        return new ApiResponse(SUCCESS, user);
    }
    
    /**
     * Endpoint to delete user.
     * 
     * @param user
     *          @ApiModel AppUser
     * 
     * @return ApiResponse
     * 
     */
    @ApiMapping("/delete")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse delete(@ApiModel AppUser user) throws Exception {
        userRepo.delete(user);
        simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

}
