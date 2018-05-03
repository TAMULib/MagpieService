package edu.tamu.app.service.repository;

import java.io.IOException;
import java.net.HttpURLConnection;

import edu.tamu.app.model.ProjectRepository;

public class FedoraSpotlightRepository extends AbstractFedoraRepository {

    public FedoraSpotlightRepository(ProjectRepository projectRepository) {
        super(projectRepository);
    }

    @Override
    protected synchronized String prepForPush() throws IOException {
        String transactionalUrl = startTransaction();
        confirmProjectContainerExists(transactionalUrl);
        return transactionalUrl;
    }

    @Override
    protected String createItemContainer(String slugName, String transactionalUrl) throws IOException {
        return createContainer(transactionalUrl, slugName);
    }

    @Override
    protected String createContainer(String containerUrl, String slugName) throws IOException {
        HttpURLConnection connection = buildFedoraConnection(containerUrl, "POST");
        connection.setRequestProperty("Accept", "*/*");
        if (slugName != null) {
            connection.setRequestProperty("slug", slugName);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != 201) {
            throw new IOException("Could not create container. Server responded with " + responseCode);
        }
        return connection.getHeaderField("Location");
    }

}
