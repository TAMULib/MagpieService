package edu.tamu.app.observer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.annotation.Scheduled;
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
        dismiss(listener.getPath());
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

    @Scheduled(fixedDelay = 900000, initialDelay = 15000)
    void healthCheck() throws Exception {
        logger.info("File monitor health check");
        if (!fileMonitorManager.isAlive()) {
            logger.warn("File monitor thread has stopped!");
            logger.info("Dismissing all listeners");
            FileAlterationMonitor monitor = fileMonitorManager.getMonitor();
            List<FileListener> listeners = new ArrayList<FileListener>();
            for (FileAlterationObserver observer : monitor.getObservers()) {
                if (observer.getDirectory().exists()) {
                    for (FileAlterationListener listener : observer.getListeners()) {
                        listeners.add((FileListener) listener);
                        dismiss((FileListener) listener);
                    }
                }
                monitor.removeObserver(observer);
            }
            fileMonitorManager.stop();
            fileMonitorManager.createMonitor();
            fileMonitorManager.start();
            listeners.forEach(this::register);
        }
        for (FileAlterationObserver observer : fileMonitorManager.getMonitor().getObservers()) {
            if (observer.getDirectory().exists()) {
                logger.info(String.format("Observer %s running", observer.getDirectory().getPath()));
                for (FileAlterationListener listener : observer.getListeners()) {
                    logger.info(String.format("\t%s", listener.getClass().getSimpleName()));
                }
            } else {
                logger.warn(String.format("Observer directory %s does not exist!", observer.getDirectory().getParent()));
                dismiss(observer.getDirectory().getPath());
            }
        }
    }

}
