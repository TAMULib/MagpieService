package edu.tamu.app.service.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

public class FedoraRepository extends AbstractFedoraRepository {

    @Autowired
    private ConfigurableMimeFileTypeMap configurableMimeFileTypeMap;

    public FedoraRepository(String repoUrl, String restPath, String containerPath, String username, String password) {
        setRepoUrl(repoUrl);
        setRestPath(restPath);
        setContainerPath(containerPath);
        setUsername(username);
        setPassword(password);
    }

    protected void prepForPush() throws IOException {
        confirmProjectContainerExists();
    }

    protected String createResource(String filePath, String containerUrl, String slug) throws IOException {
        File file = getResourceLoader().getResource("classpath:static" + filePath).getFile();
        FileInputStream fileStrm = new FileInputStream(file);
        byte[] fileBytes = IOUtils.toByteArray(fileStrm);

        HttpURLConnection connection = buildFedoraConnection(containerUrl + File.separator + slug, "PUT");

        connection.setRequestProperty("CONTENT-TYPE", configurableMimeFileTypeMap.getContentType(file));
        connection.setRequestProperty("Accept", null);

        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        os.write(fileBytes);
        os.close();

        StringWriter writer = new StringWriter();
        IOUtils.copy(connection.getInputStream(), writer, "UTF-8");
        return connection.getHeaderField("Location");
    }

    protected String createItemContainer(String slugName) throws IOException {
        return createContainer(buildContainerUrl(), slugName);
    }

    protected String createContainer(String containerUrl, String slug) throws IOException {
        HttpURLConnection connection = buildFedoraConnection(containerUrl + File.separator + slug, "PUT");

        connection.setRequestProperty("Accept", null);

        int responseCode = connection.getResponseCode();

        if (responseCode != 201) {
            throw new IOException("Could not create container. Server responded with " + responseCode);
        }

        StringWriter writer = new StringWriter();
        IOUtils.copy(connection.getInputStream(), writer, "UTF-8");
        return connection.getHeaderField("Location");
    }

}
