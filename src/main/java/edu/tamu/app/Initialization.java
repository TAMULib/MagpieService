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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.MapWatcherManagerService;
import edu.tamu.app.service.MapWatcherService;
import edu.tamu.app.service.SyncService;
import edu.tamu.app.service.WatcherManagerService;
import edu.tamu.app.service.WatcherService;

@Component
@Profile(value = { "!test" })
public class Initialization implements CommandLineRunner {

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

    @Override
    public void run(String... args) throws Exception {

        if (appContext == null) {
            logger.warn("APP CONTEXT IS NULL");
        }

        if (appContext != null && createSymlink.equals("true")) {

            try {
                FileUtils.deleteDirectory(new File(appContext.getResource("classpath:static").getFile().getAbsolutePath() + mount));
            } catch (IOException e) {
                logger.error("\nDIRECTORY DOES NOT EXIST\n", e);
            }

            try {
                Files.createSymbolicLink(Paths.get(appContext.getResource("classpath:static").getFile().getAbsolutePath() + mount), Paths.get("/mnt" + mount));
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

}