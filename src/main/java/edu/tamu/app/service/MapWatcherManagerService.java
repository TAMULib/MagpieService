package edu.tamu.app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MapWatcherManagerService {

	List<String> activeMapWatcherServices = new ArrayList<String>();
	
	public MapWatcherManagerService() { }

	public List<String> getActiveMapWatcherServices() {
		return activeMapWatcherServices;
	}

	public void setActiveMapWatcherServices(List<String> activeMapWatcherServices) {
		this.activeMapWatcherServices = activeMapWatcherServices;
	}
	
	public void addActiveWatcherService(String mapWatcherService) {
		activeMapWatcherServices.add(mapWatcherService);
	}
	
	public void removeActiveWatcherService(String mapWatcherService) {
		activeMapWatcherServices.remove(mapWatcherService);
	}
	
	public boolean isMapWatcherServiceActive(String mapWatcherService) {
		return activeMapWatcherServices.contains(mapWatcherService);
	}
	
}
