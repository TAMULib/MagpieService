package edu.tamu.app.service.suggestor;

import java.util.List;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Suggestion;
import edu.tamu.app.service.registry.MagpieService;

public interface Suggestor extends MagpieService {

    public List<Suggestion> suggest(Document document);

}
