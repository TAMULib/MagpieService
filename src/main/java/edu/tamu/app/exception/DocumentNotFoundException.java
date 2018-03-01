package edu.tamu.app.exception;

public class DocumentNotFoundException extends Exception {

    private static final long serialVersionUID = -3694750907290942734L;

    public DocumentNotFoundException(String projectName, String documentName) {
        super("Unable to find document " + documentName + " for project " + projectName);
    }

}
