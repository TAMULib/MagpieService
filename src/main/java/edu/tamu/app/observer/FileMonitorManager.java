package edu.tamu.app.observer;

import java.util.Optional;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileMonitorManager {

    private static final Logger logger = Logger.getLogger(FileMonitorManager.class);

    private static SimpleThreadFactory threadFactory;

    private static FileAlterationMonitor monitor;

    @Value("${app.polling.interval}")
    private Long interval;

    public FileMonitorManager() {

    }

    @PostConstruct
    public void createMonitor() {
        threadFactory = new SimpleThreadFactory();
        monitor = new FileAlterationMonitor(interval);
        monitor.setThreadFactory(threadFactory);
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

    public boolean isAlive() {
        return threadFactory.isMonitorThreadAlive();
    }

    public FileAlterationMonitor getMonitor() {
        return monitor;
    }

    public Optional<FileAlterationObserver> getObserver(String path) {
        Optional<FileAlterationObserver> observer = Optional.empty();
        for (FileAlterationObserver fao : monitor.getObservers()) {
            if (fao.getDirectory().getAbsolutePath().equals(path)) {
                observer = Optional.of(fao);
                break;
            }
        }
        return observer;
    }

    class SimpleThreadFactory implements ThreadFactory {
        private Thread monitorThread;

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            if (r instanceof FileAlterationMonitor) {
                monitorThread = thread;
            }

            return thread;
        }

        public boolean isMonitorThreadAlive() {
            boolean isAlive = false;
            if (monitorThread != null) {
                isAlive = monitorThread.isAlive();
            }
            return isAlive;
        }
    }

}
