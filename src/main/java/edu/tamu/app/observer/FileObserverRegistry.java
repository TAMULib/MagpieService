package edu.tamu.app.observer;

import static edu.tamu.app.Initialization.ASSETS_PATH;
import static edu.tamu.app.Initialization.LISTENER_INTERVAL;
import static edu.tamu.app.Initialization.MAPS_PATH;
import static edu.tamu.app.Initialization.PROJECTS_PATH;

import java.io.File;
import java.util.Optional;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import edu.tamu.app.service.SyncService;

@Service
public class FileObserverRegistry {

    private static final Logger logger = Logger.getLogger(FileObserverRegistry.class);

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private SyncService syncService;

    private FileMonitorManager fileMonitorManager;

    public void start() throws Exception {
        logger.info("Starting file monitor");
        fileMonitorManager = new FileMonitorManager(LISTENER_INTERVAL);
        register(new ProjectListener(ASSETS_PATH, PROJECTS_PATH));
        register(new MapFileListener(ASSETS_PATH, MAPS_PATH));
        syncService.sync();
        // NOTE: this must be last, otherwise it will invoke all file observers
        fileMonitorManager.start();
    }

    public void stop() throws Exception {
        if (fileMonitorManager != null) {
            logger.info("Stopping file monitor");
            fileMonitorManager.stop();
        }
    }

    public void restart() throws Exception {
        logger.info("Restarting file monitor");
        logger.info("Removing all observers");
        for (FileAlterationObserver observer : fileMonitorManager.getObservers()) {
            fileMonitorManager.removeObserver(observer);
        }
        stop();
        start();
    }

    public void register(FileListener listener) {
        String path = listener.getPath();
        try {
            dismiss(path);
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

    public void dismiss(String path) throws Exception {
        Optional<FileAlterationObserver> observer = fileMonitorManager.getObserver(path);
        if (observer.isPresent()) {
            logger.info("Dismissing listener: " + path);
            observer.get().getListeners().forEach(fileListener -> {
                beanFactory.destroyBean(fileListener);
            });
            observer.get().destroy();
            fileMonitorManager.removeObserver(observer.get());
        } else {
            logger.info("No listener to dismiss: " + path);
        }
    }

    @Scheduled(fixedDelayString = "${app.monitor.health.interval:900000}", initialDelayString = "${app.monitor.health.initDelay:15000}")
    void healthCheck() throws Exception {
        logger.info("File monitor health check");
        if (!fileMonitorManager.isAlive()) {
            logger.warn("File monitor thread has stopped!");
            restart();
        }
        for (FileAlterationObserver observer : fileMonitorManager.getObservers()) {
            if (observer.getDirectory().exists()) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Observing %s", observer.getDirectory().getName()));
                    for (FileAlterationListener listener : observer.getListeners()) {
                        logger.debug(String.format("\twith %s", listener.getClass().getSimpleName()));
                    }
                }
            } else {
                logger.warn(String.format("Observer directory %s does not exist!", observer.getDirectory().getParent()));
            }
        }
    }

}
