package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;

public class Suggestion {

    private final String label;

    private final String value;

    private int occurrences;

    private List<Suggestion> synonyms;

    public Suggestion(String label, String value) {
        this.label = label;
        this.value = value;
        synonyms = new ArrayList<Suggestion>();
    }

    public Suggestion(String label, String value, int occurrences) {
        this(label, value);
        this.occurrences = occurrences;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public void incrementOccurrence() {
        this.occurrences++;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public List<Suggestion> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<Suggestion> synonyms) {
        this.synonyms = synonyms;
    }

    public void addSynonym(Suggestion synonym) {
        if (!this.synonyms.contains(synonym)) {
            this.synonyms.add(synonym);
        }
    }

    public void removeSynonym(Suggestion synonym) {
        this.synonyms.remove(synonym);
    }

}
