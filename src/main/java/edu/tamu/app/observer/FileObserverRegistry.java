package edu.tamu.app.observer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

@Service
public class FileObserverRegistry {

    private static final Logger logger = Logger.getLogger(FileObserverRegistry.class);

    private static final Map<String, FileAlterationObserver> observers = new HashMap<String, FileAlterationObserver>();

    @Autowired
    private FileMonitorManager fileMonitorManager;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    public void register(FileListener listener) {
        beanFactory.autowireBean(listener);
        logger.info("Registering: " + listener.getPath());
        final File directory = new File(listener.getPath());
        if (directory.exists()) {
            FileAlterationObserver observer = new FileAlterationObserver(directory);
            observer.addListener(listener);
            observers.put(listener.getPath(), observer);
            fileMonitorManager.addObserver(observer);
            logger.info("Listening: " + listener.getPath());
        } else {
            logger.error("Path not found: " + listener.getPath());
        }
    }

    public void dismiss(FileListener listener) throws Exception {
        dismiss(listener.getPath());
    }

    public void dismiss(String path) throws Exception {
        logger.info("Dismissing: " + path);
        FileAlterationObserver observer = observers.get(path);
        observers.remove(observer.getDirectory().getAbsolutePath());
        fileMonitorManager.removeObserver(observer);
        observer.destroy();
    }

}
