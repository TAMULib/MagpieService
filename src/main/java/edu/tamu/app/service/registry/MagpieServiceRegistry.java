package edu.tamu.app.service.registry;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectService;
import edu.tamu.app.model.ProjectSuggestor;
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
            service = (MagpieService) new VoyagerAuthority((ProjectAuthority) projectService);
            break;
        case CSV:
            service = (MagpieService) new CSVAuthority((ProjectAuthority) projectService);
            break;
        case NALT:
            service = (MagpieService) new NALTSuggestor((ProjectSuggestor) projectService);
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
