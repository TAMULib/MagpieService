/* 
 * ContextInitializedHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.MapWatcherManagerService;
import edu.tamu.app.service.MapWatcherService;
import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.WatcherManagerService;
import edu.tamu.app.service.WatcherService;
import edu.tamu.framework.CoreContextInitializedHandler;

/**
 * Handler for when the servlet context refreshes.
 * 
 * @author
 *
 */
@Component
@Profile(value = { "!test" })
public class AppContextInitializedHandler extends CoreContextInitializedHandler {

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private WatcherManagerService watcherManagerService;

    @Autowired
    private MapWatcherManagerService mapWatcherManagerService;

    @Autowired
    private SyncService syncService;

    @Autowired
    private WatcherService watcherService;

    @Autowired
    private MapWatcherService mapWatcherService;

    @Value("${app.mount}")
    private String mount;

    @Value("${app.symlink.create}")
    private String createSymlink;

    private static final Logger logger = Logger.getLogger(AppContextInitializedHandler.class);

    /**
     * Method for event context refreshes.
     * 
     * @param event
     *            ContextRefreshedEvent
     * 
     */
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (appContext == null) {
            logger.warn("APP CONTEXT IS NULL");
        }

        if (createSymlink.equals("true")) {

            try {
                FileUtils.deleteDirectory(new File(event.getApplicationContext().getResource("classpath:static").getFile().getAbsolutePath() + mount));
            } catch (IOException e) {
                logger.error("\nDIRECTORY DOES NOT EXIST\n", e);
            }

            try {
                Files.createSymbolicLink(Paths.get(event.getApplicationContext().getResource("classpath:static").getFile().getAbsolutePath() + mount), Paths.get("/mnt" + mount));
            } catch (FileAlreadyExistsException e) {
                logger.error("\nSYMLINK ALREADY EXISTS\n", e);
            } catch (IOException e) {
                logger.error("\nFAILED TO CREATE SYMLINK!!!\n", e);
            }
        }

        executorService.submit(syncService);

        if (logger.isDebugEnabled()) {
            logger.debug("Watching: projects");
        }

        watcherService.setFolder("projects");

        executorService.submit(watcherService);

        watcherManagerService.addActiveWatcherService("projects");

        if (logger.isDebugEnabled()) {
            logger.debug("Watching: maps");
        }

        mapWatcherService.setFolder("maps");

        executorService.submit(mapWatcherService);

        mapWatcherManagerService.addActiveWatcherService("maps");

    }

    @Override
    protected void before(ContextRefreshedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void after(ContextRefreshedEvent event) {
        // TODO Auto-generated method stub

    }

}
