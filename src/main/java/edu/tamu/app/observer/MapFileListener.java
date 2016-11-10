package edu.tamu.app.observer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import edu.tamu.app.service.MapsService;

@Component
@Scope("prototype")
public class MapFileListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(MapFileListener.class);

    @Autowired
    private MapsService mapsService;

    public MapFileListener(String root, String folder) {
        this.root = root;
        this.folder = folder;
    }

    @Override
    public void onStart(FileAlterationObserver observer) {

    }

    @Override
    public void onDirectoryCreate(File directory) {

    }

    @Override
    public void onDirectoryChange(File directory) {

    }

    @Override
    public void onDirectoryDelete(File directory) {

    }

    @Override
    public void onFileCreate(File file) {
        logger.info("Reading map file: " + file.getName());
        try {
            mapsService.readMapFile(file);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void onFileChange(File file) {

    }

    @Override
    public void onFileDelete(File file) {

    }

    @Override
    public void onStop(FileAlterationObserver observer) {

    }

}
