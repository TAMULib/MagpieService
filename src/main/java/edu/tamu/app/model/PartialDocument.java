package edu.tamu.app.model;

public class PartialDocument {
	
	private String name;
	private String status;
	private String annotator;
	
	public PartialDocument() { }
	
	public PartialDocument(String name, String status, String annotator) {
		setName(name);
		setStatus(status);
		setAnnotator(annotator);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAnnotator() {
		return annotator;
	}

	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}

}
