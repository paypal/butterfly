package com.paypal.butterfly.facade;

import java.io.File;

/**
 * Butterfly façade
 *
 * @author facarvalho
 */
public interface ButterflyFacade {

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     */
    void transform(File applicationFolder, String templateClassName) throws IllegalAccessException, InstantiationException, ClassNotFoundException;

}