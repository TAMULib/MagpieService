package edu.tamu.app.observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.utilities.FileSystemUtility;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class FileMonitorManagerTest {

    private FileMonitorManager manager;

    private File directory;

    @Before
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

    @After
    public void cleanUp() throws IOException {
        if (directory.exists()) {
            FileSystemUtility.deleteDirectory(directory.getAbsolutePath());
        }
    }

}
