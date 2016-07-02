package com.paypal.butterfly.facade;

import com.paypal.butterfly.extensions.api.Extension;

import java.io.File;
import java.util.Set;

/**
 * Butterfly façade
 *
 * @author facarvalho
 */
public interface ButterflyFacade {

    Set<Extension> getRegisteredExtensions();

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     */
    void transform(File applicationFolder, String templateClassName) throws IllegalAccessException, InstantiationException, ClassNotFoundException;

}