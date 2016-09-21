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
     * @param shibObj
     * @Shib Object
     * 
     * @return ApiResponse
     * 
     * @throws Exception
     * 
     */
    @ApiMapping("/credentials")
    @Auth(role = "ROLE_USER")
    public ApiResponse credentials(@ApiCredentials Credentials credentials) throws Exception {
        return new ApiResponse(SUCCESS, credentials);
    }

    /**
     * Endpoint to return all users.
     * 
     * @param message
     *            Message<?>
     * 
     * @return ApiResponse
     * 
     * @throws Exception
     * 
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_USER")
    public ApiResponse allUsers() throws Exception {
        return new ApiResponse(SUCCESS, userRepo.findAll());
    }

    /**
     * Endpoint to update users role.
     * 
     * @param message
     *            Message<?>
     * 
     * @return ApiResponse
     * 
     * @throws Exception
     * 
     */
    @ApiMapping("/update")
    @Auth(role = "ROLE_USER")
    public ApiResponse update(@ApiModel AppUser user) throws Exception {
        user = userRepo.save(user);
        simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));
        return new ApiResponse(SUCCESS, user);
    }
    
    @ApiMapping("/delete")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse delete(@ApiModel AppUser user) throws Exception {
        userRepo.delete(user);
        simpMessagingTemplate.convertAndSend("/channel/user", new ApiResponse(SUCCESS, userRepo.findAll()));
        return new ApiResponse(SUCCESS);
    }

}
