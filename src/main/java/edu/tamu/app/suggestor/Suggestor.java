package edu.tamu.app.suggestor;

import java.util.List;

import edu.tamu.app.model.Document;
import edu.tamu.app.model.Suggestion;

public interface Suggestor {
	
	public List<Suggestion> suggest(Document document);
	
}
