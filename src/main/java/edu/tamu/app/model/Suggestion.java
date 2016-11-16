package edu.tamu.app.model;

public class Suggestion {
	
	private final String label;
	
	private final String value;
	
	private int occurrences;
	
	public Suggestion(String label, String value) {
		this.label = label;
		this.value = value;
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

}
