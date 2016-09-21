package edu.tamu.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class WatcherManagerService {

    List<String> activeWatcherServices = new ArrayList<String>();

    public WatcherManagerService() {
    }

    public List<String> getActiveWatcherServices() {
        return activeWatcherServices;
    }

    public void setActiveWatcherServices(List<String> activeWatcherServices) {
        this.activeWatcherServices = activeWatcherServices;
    }

    public void addActiveWatcherService(String watcherService) {
        activeWatcherServices.add(watcherService);
    }

    public void removeActiveWatcherService(String watcherService) {
        activeWatcherServices.remove(watcherService);
    }

    public boolean isWatcherServiceActive(String watcherService) {
        return activeWatcherServices.contains(watcherService);
    }

}
