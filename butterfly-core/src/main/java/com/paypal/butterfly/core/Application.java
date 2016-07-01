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
        this.folder = applicationFolder;
    }

    @Override
    public String toString() {
        return folder.getAbsolutePath();
    }

}
