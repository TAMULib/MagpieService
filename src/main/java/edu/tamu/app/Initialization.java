package edu.tamu.app;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import edu.tamu.app.observer.FileMonitorManager;
import edu.tamu.app.observer.FileObserverRegistry;
import edu.tamu.app.observer.MapFileListener;
import edu.tamu.app.observer.ProjectFileListener;
import edu.tamu.app.service.SyncService;

@Component
@Profile(value = { "!test" })
public class Initialization implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(Initialization.class);

    @Value("${app.mount}")
    private String mount;

    @Value("${app.symlink.create}")
    private String link;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private SyncService syncService;

    @Autowired
    private FileMonitorManager fileMonitorManager;

    @Autowired
    private FileObserverRegistry fileObserverRegistry;

    @Override
    public void run(String... args) throws Exception {

        if (link.equals("true")) {
            try {
            	Files.delete(Paths.get(resourceLoader.getResource("classpath:static" + mount).getURL().getPath()));
            } catch (IOException e) {
                logger.error("\nDIRECTORY DOES NOT EXIST\n", e);
            }

            try {
                Files.createSymbolicLink(Paths.get(resourceLoader.getResource("classpath:static" + mount).getURL().getPath()), Paths.get("/mnt" + mount));
            } catch (FileAlreadyExistsException e) {
                logger.error("\nSYMLINK ALREADY EXISTS\n", e);
            } catch (IOException e) {
                logger.error("\nFAILED TO CREATE SYMLINK!!!\n", e);
            }
        }

        syncService.sync();

        String root = resourceLoader.getResource("classpath:static" + mount).getURL().getPath();

        fileObserverRegistry.register(new ProjectFileListener(root, "projects"));
        fileObserverRegistry.register(new MapFileListener(root, "maps"));

        fileMonitorManager.start();

    }

}