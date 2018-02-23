package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.service.SyncService;
import edu.tamu.weaver.response.ApiResponse;

/**
 * Admin Controller.
 * 
 * @author
 *
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = Logger.getLogger(AdminController.class);

    @Autowired
    private SyncService syncService;

    // TODO: handle exception gracefully
    /**
     * Synchronizes the project directory with the database.
     * 
     * @return ApiResponse
     * @throws IOException
     * 
     */
    @RequestMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse syncDocuments() throws IOException {
        logger.info("Syncronizing projects with database.");
        syncService.sync();
        return new ApiResponse(SUCCESS, "Syncronized projects with database.");
    }

}
