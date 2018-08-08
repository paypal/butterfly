package com.paypal.butterfly.core;

import com.paypal.butterfly.api.Application;

import java.io.File;

/**
 * The application to be transformed
 *
 * @author facarvalho
 */
class ApplicationImpl implements Application {

    private File folder;

    ApplicationImpl(File applicationFolder) {
        setFolder(applicationFolder);
    }

    @Override
    public File getFolder() {
        return folder;
    }

    void setFolder(File folder) {
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
