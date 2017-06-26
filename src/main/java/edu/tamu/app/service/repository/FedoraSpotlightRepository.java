package edu.tamu.app.service.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

import edu.tamu.app.model.ProjectRepository;

public class FedoraSpotlightRepository extends AbstractFedoraRepository {

    @Autowired
    private ConfigurableMimeFileTypeMap configurableMimeFileTypeMap;

    public FedoraSpotlightRepository(ProjectRepository projectRepository) {
        super(projectRepository);
    }

    protected void prepForPush() throws IOException {
        startTransaction();
        confirmProjectContainerExists();
    }

    protected String createResource(String filePath, String itemContainerPath, String slug) throws IOException {

        File file = getResourceLoader().getResource("classpath:static" + filePath).getFile();
        FileInputStream fileStrm = new FileInputStream(file);
        byte[] fileBytes = IOUtils.toByteArray(fileStrm);
        HttpURLConnection connection = buildFedoraConnection(itemContainerPath, "POST");
        connection.setRequestProperty("CONTENT-TYPE", configurableMimeFileTypeMap.getContentType(file));
        connection.setRequestProperty("Accept", "*/*");

        if (slug != null) {
            connection.setRequestProperty("slug", slug);
        }

        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        os.write(fileBytes);
        os.close();

        return connection.getHeaderField("Location");
    }

    protected String createItemContainer(String slugName) throws IOException {
        return createContainer(buildContainerUrl(), slugName);
    }

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
