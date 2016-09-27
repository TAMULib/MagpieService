package edu.tamu.app.observer;

import javax.annotation.PostConstruct;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileMonitorManager {

    private static final Logger logger = Logger.getLogger(FileObserverRegistry.class);

    @Value("${app.polling.interval}")
    private Long interval;

    private FileAlterationMonitor monitor;

    public FileMonitorManager() {
    }

    @PostConstruct
    public void createMonitor() {
        monitor = new FileAlterationMonitor(interval);
    }

    public void addObserver(FileAlterationObserver observer) {
        logger.info("Adding observer: " + observer.getDirectory());
        monitor.addObserver(observer);
    }

    public void removeObserver(FileAlterationObserver observer) {
        logger.info("Removing observer: " + observer.getDirectory());
        monitor.removeObserver(observer);
    }

    public void start() throws Exception {
        logger.info("Starting monitor");
        monitor.start();
    }

    public void stop() throws Exception {
        logger.info("Stopping monitor");
        monitor.stop();
    }

    public FileAlterationMonitor getMonitor() {
        return monitor;
    }
}