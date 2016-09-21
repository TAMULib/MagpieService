/* 
 * AdminController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.service.SyncService;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.model.ApiResponse;

/**
 * Admin Controller.
 * 
 * @author
 *
 */
@RestController
@ApiMapping("/admin")
public class AdminController {

    @Autowired
    private SyncService syncService;

    @Autowired
    private ExecutorService executorService;

    private static final Logger logger = Logger.getLogger(AdminController.class);

    /**
     * Synchronizes the project directory with the database.
     * 
     * @return ApiResponse
     * 
     * @throws Exception
     * 
     */
    @ApiMapping("/sync")
    @Auth(role = "ROLE_ADMIN")
    public ApiResponse syncDocuments() throws Exception {

        logger.info("Syncronizing projects with database.");

        executorService.submit(syncService);

        return new ApiResponse(SUCCESS);
    }

}
