package edu.tamu.app.observer;

import java.util.Optional;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;

public class FileMonitorManager {

    private static final Logger logger = Logger.getLogger(FileMonitorManager.class);

    private final FileAlterationMonitor monitor;

    private final SimpleThreadFactory threadFactory;

    public FileMonitorManager(long interval) {
        monitor = new FileAlterationMonitor(interval);
        threadFactory = new SimpleThreadFactory();
        monitor.setThreadFactory(threadFactory);
    }

    public void start() throws Exception {
        logger.info("Starting monitor");
        monitor.start();
    }

    public void stop() throws Exception {
        logger.info("Stopping monitor");
        monitor.stop();
    }

    public void addObserver(FileAlterationObserver observer) {
        logger.info("Adding observer: " + observer.getDirectory());
        monitor.addObserver(observer);
    }

    public void removeObserver(FileAlterationObserver observer) {
        logger.info("Removing observer: " + observer.getDirectory());
        monitor.removeObserver(observer);
    }

    public boolean isAlive() {
        return threadFactory.isMonitorThreadAlive();
    }

    public Iterable<FileAlterationObserver> getObservers() {
        return monitor.getObservers();
    }

    public Optional<FileAlterationObserver> getObserver(String path) {
        Optional<FileAlterationObserver> observer = Optional.empty();
        for (FileAlterationObserver fao : getObservers()) {
            if (fao.getDirectory().getAbsolutePath().equals(path)) {
                observer = Optional.of(fao);
                break;
            }
        }
        return observer;
    }

    class SimpleThreadFactory implements ThreadFactory {
        private Thread monitorThread;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            if (r instanceof FileAlterationMonitor) {
                monitorThread = thread;
            }
            return thread;
        }

        private boolean isMonitorThreadAlive() {
            boolean isAlive = false;
            if (monitorThread != null) {
                isAlive = monitorThread.isAlive();
            }
            return isAlive;
        }

    }

}
