package edu.tamu.app;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import edu.tamu.app.observer.FileObserverRegistry;
import edu.tamu.app.utilities.FileSystemUtility;

@Component
public class Initialization implements CommandLineRunner {

    public static String HOST;

    public static String ASSETS_PATH;

    public static String PROJECTS_PATH = "projects";

    public static String PROJECTS_JSON_PATH;
    
    public static String MAPS_PATH = "maps";

    public static int LISTENER_PARALLELISM = 10;

    public static long LISTENER_INTERVAL = 1000;

    protected static final Logger logger = LoggerFactory.getLogger(Initialization.class);

    @Value("${app.host}")
    private String host;

    @Value("${app.assets.path}")
    private String assetsPath;

    @Value("${app.projectsjson.path}")
    private String projectsJsonPath;

    @Value("${app.listener.parallelism:10}")
    private int listenerParallelism;

    @Value("${app.polling.interval:1000}")
    private long listenerInterval;

    @Value("${app.assets.folders}")
    private String[] assetsFolders;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private FileObserverRegistry fileObserverRegistry;

    @Override
    public void run(String... args) throws Exception {
        setHost();
        setAssetsPath();
        setProjectsJsonPath();
        setListenerParallelism();
        setListenerInterval();

        for (String folder : assetsFolders) {
            FileSystemUtility.createDirectory(ASSETS_PATH + File.separator + folder);
        }

        fileObserverRegistry.start();
    }

    private void setHost() {
        HOST = host;
    }

    private void setAssetsPath() {
        logger.debug("Initialization runner setting assets path " + assetsPath);
        try {
            ASSETS_PATH = resourceLoader.getResource(assetsPath).getURI().getPath();
        } catch (IOException e) {
            ASSETS_PATH = assetsPath;
        }
        if (ASSETS_PATH.endsWith(File.separator)) {
            ASSETS_PATH = ASSETS_PATH.substring(0, ASSETS_PATH.length() - 1);
        }
    }

    private void setProjectsJsonPath() {
        logger.debug("Initialization runner setting initial projects json file path" + projectsJsonPath);
        try {
            PROJECTS_JSON_PATH = resourceLoader.getResource(projectsJsonPath).getURI().getPath();

        } catch (IOException e) {
            PROJECTS_JSON_PATH = projectsJsonPath;
        }
    }

    private void setListenerParallelism() {
        logger.debug("Initialization runner setting listener parallelism " + listenerParallelism);
        LISTENER_PARALLELISM = listenerParallelism;
    }

    private void setListenerInterval() {
        logger.debug("Initialiazion runner setting listener interval " + listenerInterval);
        LISTENER_INTERVAL = listenerInterval;
    }
}