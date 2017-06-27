package edu.tamu.app.service.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.Project;
import edu.tamu.app.model.ProjectAuthority;
import edu.tamu.app.model.ProjectRepository;
import edu.tamu.app.model.ProjectService;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.service.MapFileService;
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

    private Map<String, MagpieAuxiliaryService> auxiliaryServices;

    public MagpieServiceRegistry() {
        services = new HashMap<String, MagpieService>();
        auxiliaryServices = new HashMap<String, MagpieAuxiliaryService>();
    }

    public void register(Project project, ProjectService projectService) {

        logger.info("Registering service: " + projectService.getName());

        Optional<MagpieService> service = Optional.empty();

        Optional<MagpieAuxiliaryService> auxiliaryService = Optional.empty();

        // TODO: devise a way to not have to switch on ServiceType

        switch (projectService.getType()) {
        case DSPACE:
            auxiliaryService = Optional.of((MagpieAuxiliaryService) new MapFileService(project, (ProjectRepository) projectService));
            service = Optional.of((MagpieService) new DSpaceRepository((ProjectRepository) projectService));
            break;
        case FEDORA_SPOTLIGHT:
            service = Optional.of((MagpieService) new FedoraSpotlightRepository((ProjectRepository) projectService));
            break;
        case FEDORA_PCDM:
            service = Optional.of((MagpieService) new FedoraPCDMRepository((ProjectRepository) projectService));
            break;
        case VOYAGER:
            service = Optional.of((MagpieService) new VoyagerAuthority((ProjectAuthority) projectService));
            break;
        case CSV:
            service = Optional.of((MagpieService) new CSVAuthority((ProjectAuthority) projectService));
            break;
        case NALT:
            service = Optional.of((MagpieService) new NALTSuggestor((ProjectSuggestor) projectService));
            break;
        case ARCHIVEMATICA:
            service = Optional.of((MagpieService) new ArchivematicaFilesystemRepository((ProjectRepository) projectService));
            break;
        default:
            logger.info("Unidentified service type: " + projectService.getType());
            break;
        }

        if (auxiliaryService.isPresent()) {
            beanFactory.autowireBean(auxiliaryService.get());
            beanFactory.initializeBean(auxiliaryService.get(), project.getName() + "AuxiliaryService");
            auxiliaryServices.put(project.getName(), auxiliaryService.get());
        }

        if (service.isPresent()) {
            beanFactory.autowireBean(service.get());
            services.put(projectService.getName(), service.get());
        } else {
            logger.info("Service was not instantiated!");
        }
    }

    public MagpieService getService(String name) {
        return services.get(name);
    }

    public MagpieAuxiliaryService getAuxiliaryService(String name) {
        return auxiliaryServices.get(name);
    }

}
