package edu.tamu.app.observer;

import java.io.File;
import java.util.Optional;

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

    public void register(FileListener listener) {
        String path = listener.getPath();
        try {
            dismiss(listener);
        } catch (Exception e) {
            logger.error("Unable to dismiss listener: " + path);
        }
        logger.info("Registering listener: " + path);
        beanFactory.autowireBean(listener);
        final File directory = new File(path);
        if (directory.exists()) {
            FileAlterationObserver observer = new FileAlterationObserver(directory);
            observer.addListener(listener);
            fileMonitorManager.addObserver(observer);
            logger.info("Listening at: " + path);
        } else {
            logger.error("Path not found: " + path);
        }
    }

    public void dismiss(FileListener listener) throws Exception {
        String path = listener.getPath();
        Optional<FileAlterationObserver> observer = fileMonitorManager.getObserver(path);
        if (observer.isPresent()) {
            logger.info("Dismissing listener: " + path);
            observer.get().destroy();
            beanFactory.destroyBean(listener);
            fileMonitorManager.removeObserver(observer.get());
        } else {
            logger.info("No listener to dismiss: " + path);
        }
    }

}
