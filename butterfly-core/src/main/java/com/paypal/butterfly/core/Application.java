package com.paypal.butterfly.core;

import java.io.File;

/**
 * The application to be transformed
 *
 * @author facarvalho
 */
public class Application {

    private String name;

    private File folder;

    public Application(File applicationFolder) {
        setFolder(applicationFolder);
    }

    public void setFolder(File folder) {

        this.folder = folder;
    }
}
