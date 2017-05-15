package edu.tamu.app.service.registry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.ProjectService;
import edu.tamu.app.service.authority.CSVAuthority;
import edu.tamu.app.service.authority.VoyagerAuthority;
import edu.tamu.app.service.repository.ArchivematicaFilesystemRepository;
import edu.tamu.app.service.repository.DSpaceRepository;
import edu.tamu.app.service.repository.FedoraPCDMRepository;
import edu.tamu.app.service.repository.FedoraRepository;
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
            service = (MagpieService) new DSpaceRepository(
                    projectService.getSettingValues("repoUrl").size() > 0
                            ? projectService.getSettingValues("repoUrl").get(0) : "",
                    projectService.getSettingValues("repoUIPath").size() > 0
                            ? projectService.getSettingValues("repoUIPath").get(0) : "",
                    projectService.getSettingValues("collectionId").size() > 0
                            ? projectService.getSettingValues("collectionId").get(0) : "",
                    projectService.getSettingValues("groupId").size() > 0
                            ? projectService.getSettingValues("groupId").get(0) : "",
                    projectService.getSettingValues("userName").size() > 0
                            ? projectService.getSettingValues("userName").get(0) : "",
                    projectService.getSettingValues("password").size() > 0
                            ? projectService.getSettingValues("password").get(0) : "");
            break;
        case FEDORA:
            service = (MagpieService) new FedoraRepository(
                    projectService.getSettingValues("repoUrl").size() > 0
                            ? projectService.getSettingValues("repoUrl").get(0) : "",
                    projectService.getSettingValues("restPath").size() > 0
                            ? projectService.getSettingValues("restPath").get(0) : "",
                    projectService.getSettingValues("containerPath").size() > 0
                            ? projectService.getSettingValues("containerPath").get(0) : "",
                    projectService.getSettingValues("userName").size() > 0
                            ? projectService.getSettingValues("userName").get(0) : "",
                    projectService.getSettingValues("password").size() > 0
                            ? projectService.getSettingValues("password").get(0) : "");
            break;
        case FEDORA_PCDM:
            service = (MagpieService) new FedoraPCDMRepository(
                    projectService.getSettingValues("repoUrl").size() > 0
                            ? projectService.getSettingValues("repoUrl").get(0) : "",
                    projectService.getSettingValues("restPath").size() > 0
                            ? projectService.getSettingValues("restPath").get(0) : "",
                    projectService.getSettingValues("containerPath").size() > 0
                            ? projectService.getSettingValues("containerPath").get(0) : "",
                    projectService.getSettingValues("userName").size() > 0
                            ? projectService.getSettingValues("userName").get(0) : "",
                    projectService.getSettingValues("password").size() > 0
                            ? projectService.getSettingValues("password").get(0) : "");
            break;
        case VOYAGER:
            service = (MagpieService) new VoyagerAuthority(
                    projectService.getSettingValues("host").size() > 0 ? projectService.getSettingValues("host").get(0)
                            : "",
                    projectService.getSettingValues("port").size() > 0 ? projectService.getSettingValues("port").get(0)
                            : "",
                    projectService.getSettingValues("app").size() > 0 ? projectService.getSettingValues("app").get(0)
                            : "");
            break;
        case CSV:
            service = (MagpieService) new CSVAuthority(projectService.getSettingValues("paths"),
                    projectService.getSettingValues("identifier").size() > 0
                            ? projectService.getSettingValues("identifier").get(0) : "filename",
                    projectService.getSettingValues("delimeter").size() > 0
                            ? projectService.getSettingValues("delimeter").get(0) : "||");
            break;
        case NALT:
            service = (MagpieService) new NALTSuggestor(
                    projectService.getSettingValues("pelicanUrl").size() > 0
                            ? projectService.getSettingValues("pelicanUrl").get(0) : "",
                    projectService.getSettingValues("subjectLabel").size() > 0
                            ? projectService.getSettingValues("subjectLabel").get(0) : "");
            break;
        case ARCHIVEMATICA:
            try {
                service = (MagpieService) new ArchivematicaFilesystemRepository(
                        projectService.getSettingValues("archivematicaDirectoryName").size() > 0
                                ? projectService.getSettingValues("archivematicaDirectoryName").get(0) : "",
                        projectService.getSettingValues("archivematicaURL").size() > 0
                                ? projectService.getSettingValues("archivematicaURL").get(0) : "",
                        projectService.getSettingValues("archivematicaUsername").size() > 0
                                ? projectService.getSettingValues("archivematicaUsername").get(0) : "",
                        projectService.getSettingValues("archivematicaAPIKey").size() > 0
                                ? projectService.getSettingValues("archivematicaAPIKey").get(0) : "",
                        projectService.getSettingValues("archivematicaTransferSourceLocationUUID").size() > 0
                                ? projectService.getSettingValues("archivematicaTransferSourceLocationUUID").get(0) : "",
                        projectService.getSettingValues("archivematicaTransferLocationDirectoryName").size() > 0
                                ? projectService.getSettingValues("archivematicaTransferLocationDirectoryName").get(0) : ""
                                        );
            } catch (IOException e) {
                logger.error("Could not instantiate Archivematica Repository due to IO exception:");
                e.printStackTrace();
            }
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
