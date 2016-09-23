package edu.tamu.app.observer;

import org.apache.commons.io.monitor.FileAlterationListener;

public interface FileListener extends FileAlterationListener {

    public String getRoot();

    public void setRoot(String path);

    public String getFolder();

    public void setFolder(String folder);

    public String getPath();

}
