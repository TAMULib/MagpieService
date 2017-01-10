package edu.tamu.app;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
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
import edu.tamu.app.utilities.FileSystemUtility;

@Component
@Profile(value = { "!test" })
public class Initialization implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(Initialization.class);

    @Value("${app.mount.root}")
    private String root;

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

        Path projectPath = FileSystemUtility.getWindowsSafePath(resourceLoader.getResource("classpath:static" + mount).getURL().getPath()).toAbsolutePath();

        if (link.equals("true")) {
            try {
                logger.info("Attempting to delete directory: " + projectPath.toString());
                FileUtils.deleteDirectory(projectPath.toFile());
            } catch (IOException e) {
                logger.error("\nDIRECTORY DOES NOT EXIST\n", e);
            }

            try {
                Path mountPath = FileSystemUtility.getWindowsSafePath(root + mount).toAbsolutePath();
                logger.info("Attempting to symlink directory: " + projectPath.toString() + " to " + mountPath.toString());
                Files.createSymbolicLink(projectPath, mountPath);
            } catch (FileAlreadyExistsException e) {
                logger.error("\nSYMLINK ALREADY EXISTS\n", e);
            } catch (IOException e) {
                logger.error("\nFAILED TO CREATE SYMLINK!!!\n", e);
            }
        }

        fileObserverRegistry.register(new ProjectFileListener(projectPath.toString(), "projects"));
        fileObserverRegistry.register(new MapFileListener(projectPath.toString(), "maps"));

        fileMonitorManager.start();

        syncService.sync();

    }

}