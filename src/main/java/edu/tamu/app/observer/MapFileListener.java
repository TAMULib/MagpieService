package edu.tamu.app.observer;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.service.MapFileService;
import edu.tamu.app.service.registry.MagpieServiceRegistry;

public class MapFileListener extends AbstractFileListener {

    private static final Logger logger = Logger.getLogger(MapFileListener.class);

    @Autowired
    private MagpieServiceRegistry magpieServiceRegistry;

    public MapFileListener(String root, String folder) {
        super(root, folder);
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
        String projectName = file.getParentFile().getName();
        logger.info("Reading project " + projectName + " map file: " + file.getName());

        Optional<MapFileService> mapFileService = Optional.ofNullable((MapFileService) magpieServiceRegistry.getAuxiliaryService(projectName));
        if (mapFileService.isPresent()) {
            try {
                mapFileService.get().readMapFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.warn("Unable to find map file service in the registry!");
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
