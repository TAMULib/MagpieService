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
import edu.tamu.app.model.Suggestion;

public class NALTSuggestor implements Suggestor {

    private String subjectLabel;

    private String pelicanTermOccurrenceUrl;

    @Autowired
    private ObjectMapper objectMapper;

    public NALTSuggestor(String pelicanTermOccurrenceUrl, String subjectLabel) {
        this.pelicanTermOccurrenceUrl = pelicanTermOccurrenceUrl;
        this.subjectLabel = subjectLabel;
    }

    @Override
    public List<Suggestion> suggest(Document document) {

        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        try {
            File file = File.createTempFile("tempFile", Long.toString(System.nanoTime()));

            file.deleteOnExit();

            FileUtils.copyURLToFile(new URL(document.getTxtUri()), file);

            String text = FileUtils.readFileToString(file, StandardCharsets.UTF_8).toLowerCase();

            JsonNode payloadNode = objectMapper.readTree(fetchNALTSuggestions(text)).get("payload");

            JsonNode termOccurrenceArrayNode = payloadNode.get("ArrayList<TermOccurrence>") != null ? payloadNode.get("ArrayList<TermOccurrence>") : payloadNode.get("ArrayList");

            if (termOccurrenceArrayNode.isArray()) {
                for (final JsonNode termOccurrenceNode : termOccurrenceArrayNode) {
                    Suggestion suggestion = new Suggestion(subjectLabel, termOccurrenceNode.get("term").textValue(), termOccurrenceNode.get("count").asInt());

                    JsonNode synonymOccurrencesNode = termOccurrenceNode.get("synonyms");
                    if (synonymOccurrencesNode.isArray()) {

                        for (final JsonNode synonymOccurrenceNode : synonymOccurrencesNode) {
                            suggestion.addSynonym(new Suggestion(subjectLabel, synonymOccurrenceNode.get("term").textValue(), synonymOccurrenceNode.get("count").asInt()));
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

        URL pelicanSuggestionUrl = new URL(pelicanTermOccurrenceUrl);

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

}
