package edu.tamu.app.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import edu.tamu.app.utilities.FileSystemUtility;

@ActiveProfiles("test")
public class FileMonitorManagerTest {

    private FileMonitorManager manager;

    private File directory;

    @BeforeEach
    public void setUp() {
        manager = new FileMonitorManager(10000);
        directory = new File("temp");
    }

    @Test
    public void testStart() throws Exception {
        manager.start();
    }

    @Test
    public void testStop() throws Exception {
        testStart();
        manager.stop();
    }

    @Test
    public void testIsAlive() throws Exception {
        testStart();
        assertTrue(manager.isAlive());
    }

    @Test
    public void testAddObserver() throws Exception {
        testStart();

        FileSystemUtility.createDirectory(directory.getAbsolutePath());

        FileAlterationObserver observer = new FileAlterationObserver(directory);

        manager.addObserver(observer);

        List<FileAlterationObserver> observers = new ArrayList<FileAlterationObserver>();
        manager.getObservers().forEach(observers::add);

        assertEquals(1, observers.size());
    }

    @Test
    public void testRemoveObserver() throws Exception {
        testAddObserver();

        List<FileAlterationObserver> observers = new ArrayList<FileAlterationObserver>();
        manager.getObservers().forEach(observers::add);

        assertEquals(1, observers.size());
    }

    @Test
    public void testGetObservers() throws Exception {
        testAddObserver();

        List<FileAlterationObserver> observers = new ArrayList<FileAlterationObserver>();
        manager.getObservers().forEach(observers::add);

        assertEquals(1, observers.size());
    }

    @Test
    public void testGetObserver() throws Exception {
        testAddObserver();

        Optional<FileAlterationObserver> observer = manager.getObserver(directory.getAbsolutePath());

        assertTrue(observer.isPresent());
    }

    @AfterEach
    public void cleanUp() throws IOException {
        if (directory.exists()) {
            FileSystemUtility.deleteDirectory(directory.getAbsolutePath());
        }
    }

}
