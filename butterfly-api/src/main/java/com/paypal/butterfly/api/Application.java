package com.paypal.butterfly.api;

import java.io.File;

/**
 * POJO holding information about the application to be transformed
 *
 * @author facarvalho
 */
public interface Application {

    /**
     * Returns the application folder
     *
     * @return the application folder
     */
    File getFolder();

}
