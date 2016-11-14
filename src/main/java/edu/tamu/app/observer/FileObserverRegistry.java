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

    @Autowired
    private FileMonitorManager fileMonitorManager;

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    private Map<String, FileAlterationObserver> observers;

    public FileObserverRegistry() {
        observers = new HashMap<String, FileAlterationObserver>();
    }

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
        logger.info("Dismissing: " + listener.getPath());
        FileAlterationObserver observer = observers.get(listener.getPath());
        observers.remove(observer.getDirectory().getAbsolutePath());
        fileMonitorManager.removeObserver(observer);
        observer.destroy();
    }

}
