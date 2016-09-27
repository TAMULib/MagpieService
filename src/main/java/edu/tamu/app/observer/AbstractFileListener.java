package edu.tamu.app.observer;

public abstract class AbstractFileListener implements FileListener {

    protected String root;

    protected String folder;

    /**
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * @param root
     *            the root to set
     */
    public void setRoot(String root) {
        this.root = root;
    }

    /**
     * @return the folder
     */
    public String getFolder() {
        return folder;
    }

    /**
     * @param folder
     *            the folder to set
     */
    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getPath() {
        return root + "/" + folder;
    }

}