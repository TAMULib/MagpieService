package edu.tamu.app;

import java.io.File;
import java.io.IOException;

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
    
    public static String MAPS_PATH = "maps";

    public static int LISTENER_PARALLELISM = 10;

    public static long LISTENER_INTERVAL = 1000;

    @Value("${app.host}")
    private String host;

    @Value("${app.assets.path}")
    private String assetsPath;

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
        setHost(host);
        setAssetsPath(assetsPath);
        setListenerParallelism(listenerParallelism);
        setListenerInterval(listenerInterval);

        for (String folder : assetsFolders) {
            FileSystemUtility.createDirectory(ASSETS_PATH + File.separator + folder);
        }

        fileObserverRegistry.start();
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
        if (ASSETS_PATH.endsWith(File.separator)) {
            ASSETS_PATH = ASSETS_PATH.substring(0, ASSETS_PATH.length() - 1);
        }
    }

    private void setListenerParallelism(int parallelism) {
        LISTENER_PARALLELISM = parallelism;
    }

    private void setListenerInterval(long interval) {
        LISTENER_INTERVAL = interval;
    }

}