package edu.tamu.app.service.registry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectService;
import edu.tamu.app.service.authority.CSVAuthority;
import edu.tamu.app.service.authority.VoyagerAuthority;
import edu.tamu.app.service.repository.ArchivematicaFilesystemRepository;
import edu.tamu.app.service.repository.DSpaceRepository;
import edu.tamu.app.service.repository.FedoraPCDMRepository;
import edu.tamu.app.service.repository.FedoraSpotlightRepository;
import edu.tamu.app.service.suggestor.NALTSuggestor;

@Service
public class MagpieServiceRegistry {

    private static final Logger logger = Logger.getLogger(MagpieServiceRegistry.class);

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    private Map<String, MagpieService> services;

    public MagpieServiceRegistry() {
        services = new HashMap<String, MagpieService>();
    }

    public void register(ProjectService projectService) {

        logger.info("Registering: " + projectService.getName());

        MagpieService service = null;

        // TODO: devise a way to not have to switch on ServiceType

        switch (projectService.getType()) {
        case DSPACE:
            service = (MagpieService) new DSpaceRepository((ProjectRepository) projectService);
            break;
        case FEDORA_SPOTLIGHT:
            service = (MagpieService) new FedoraSpotlightRepository((ProjectRepository) projectService);
            break;
        case FEDORA_PCDM:
            service = (MagpieService) new FedoraPCDMRepository((ProjectRepository) projectService);
            break;
        case VOYAGER:
            // TODO: create new constructor passing in ProjectAuthority
            service = (MagpieService) new VoyagerAuthority(projectService.getSettingValues("host").size() > 0 ? projectService.getSettingValues("host").get(0) : "", projectService.getSettingValues("port").size() > 0 ? projectService.getSettingValues("port").get(0) : "", projectService.getSettingValues("app").size() > 0 ? projectService.getSettingValues("app").get(0) : "");
            break;
        case CSV:
            // TODO: create new constructor passing in ProjectAuthority
            service = (MagpieService) new CSVAuthority(projectService.getSettingValues("paths"), projectService.getSettingValues("identifier").size() > 0 ? projectService.getSettingValues("identifier").get(0) : "filename", projectService.getSettingValues("delimeter").size() > 0 ? projectService.getSettingValues("delimeter").get(0) : "||");
            break;
        case NALT:
            // TODO: create new constructor passing in ProjectSuggestor
            service = (MagpieService) new NALTSuggestor(projectService.getSettingValues("pelicanUrl").size() > 0 ? projectService.getSettingValues("pelicanUrl").get(0) : "", projectService.getSettingValues("subjectLabel").size() > 0 ? projectService.getSettingValues("subjectLabel").get(0) : "");
            break;
        case ARCHIVEMATICA:
        	service = (MagpieService) new ArchivematicaFilesystemRepository((ProjectRepository) projectService);
            break;
        default:
            logger.info("Unidentified service type: " + projectService.getType());
            break;
        }

        if (service != null) {
            beanFactory.autowireBean(service);
            services.put(projectService.getName(), service);
        } else {
            logger.info("Service was not instantiated!");
        }
    }

    public MagpieService getService(String name) {
        return services.get(name);
    }

}
