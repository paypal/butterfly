package com.paypal.butterfly.core;

import java.io.File;

/**
 * The application to be transformed
 *
 * @author facarvalho
 */
public class Application {

    private File folder;

    public Application(File applicationFolder) {
        setFolder(applicationFolder);
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        if(folder == null || !folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException(String.format("Invalid application folder %s",folder));
        }
        this.folder = folder;
    }

    @Override
    public String toString() {
        return folder.getAbsolutePath();
    }

}
