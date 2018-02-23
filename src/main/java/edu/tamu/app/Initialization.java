package edu.tamu.app;

import java.io.File;
import java.io.IOException;

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
import edu.tamu.app.observer.ProjectListener;
import edu.tamu.app.service.SyncService;

@Component
@Profile(value = { "!test" })
public class Initialization implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(Initialization.class);

    public static String HOST;

    public static String ASSETS_PATH;

    @Value("${app.host}")
    private String host;

    @Value("${app.assets.path}")
    private String assetsPath;

    @Value("${app.assets.folders}")
    private String[] assetsFolders;

    @Autowired
    private SyncService syncService;

    @Autowired
    private FileMonitorManager fileMonitorManager;

    @Autowired
    private FileObserverRegistry fileObserverRegistry;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void run(String... args) throws Exception {

        setHost(host);

        setAssetsPath(assetsPath);

        for (String folder : assetsFolders) {
            createDirectory(ASSETS_PATH, folder);
        }

        fileObserverRegistry.register(new ProjectListener(ASSETS_PATH, "projects"));
        fileObserverRegistry.register(new MapFileListener(ASSETS_PATH, "maps"));

        fileMonitorManager.start();

        syncService.sync();
    }

    private void setHost(String host) {
        HOST = host;
    }

    private void setAssetsPath(String host) {
        try {
            ASSETS_PATH = resourceLoader.getResource(assetsPath).getURI().getPath();
        } catch (IOException e) {
            ASSETS_PATH = assetsPath;
        }
    }

    private void createDirectory(String path, String name) {
        String fullPath = path + File.separator + name;
        File directory = new File(fullPath);

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                logger.info(fullPath + " folder created");
            } else {
                logger.error("Failed to create " + fullPath);
            }
        }
    }

}