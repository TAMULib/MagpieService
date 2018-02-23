package edu.tamu.app.service.suggestor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.ProjectSuggestor;
import edu.tamu.app.model.Resource;
import edu.tamu.app.model.Suggestion;
import edu.tamu.app.model.repo.ResourceRepo;

public class NALTSuggestor implements Suggestor {

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectSuggestor projectSuggestor;

    public NALTSuggestor(ProjectSuggestor projectSuggestor) {
        this.projectSuggestor = projectSuggestor;
    }

    @Override
    public List<Suggestion> suggest(Document document) {

        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        // TODO: throw exception and handle using controller advice
        try {
            StringBuilder textBuilder = new StringBuilder();

            for (Resource resource : resourceRepo.findAllByDocumentNameAndMimeType(document.getName(), "text/plain")) {
                File file = File.createTempFile(resource.getName(), Long.toString(System.nanoTime()));
                file.deleteOnExit();
                FileUtils.copyURLToFile(new URL(resource.getUrl()), file);
                textBuilder.append(FileUtils.readFileToString(file, StandardCharsets.UTF_8).toLowerCase());
                textBuilder.append("\n\n");
            }

            JsonNode payloadNode = objectMapper.readTree(fetchNALTSuggestions(textBuilder.toString())).get("payload");

            JsonNode termOccurrenceArrayNode = payloadNode.get("ArrayList<TermOccurrence>") != null ? payloadNode.get("ArrayList<TermOccurrence>") : payloadNode.get("ArrayList");

            if (termOccurrenceArrayNode.isArray()) {
                for (final JsonNode termOccurrenceNode : termOccurrenceArrayNode) {
                    Suggestion suggestion = new Suggestion(getSubjectLabel(), termOccurrenceNode.get("term").textValue(), termOccurrenceNode.get("count").asInt());

                    JsonNode synonymOccurrencesNode = termOccurrenceNode.get("synonyms");
                    if (synonymOccurrencesNode.isArray()) {

                        for (final JsonNode synonymOccurrenceNode : synonymOccurrencesNode) {
                            suggestion.addSynonym(new Suggestion(getSubjectLabel(), synonymOccurrenceNode.get("term").textValue(), synonymOccurrenceNode.get("count").asInt()));
                        }
                    }

                    suggestions.add(suggestion);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return suggestions;
    }

    private String fetchNALTSuggestions(String text) throws IOException {

        URL pelicanSuggestionUrl = new URL(getPelicanUrl());

        HttpURLConnection connection = (HttpURLConnection) pelicanSuggestionUrl.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("Content-Length", String.valueOf(text.length()));

        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();

        os.write(text.getBytes());

        String results = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

        IOUtils.closeQuietly(connection.getInputStream());

        return results;
    }

    public String getPelicanUrl() {
        return projectSuggestor.getSettingValues("pelicanUrl").get(0);
    }

    public String getSubjectLabel() {
        return projectSuggestor.getSettingValues("subjectLabel").get(0);
    }

}
