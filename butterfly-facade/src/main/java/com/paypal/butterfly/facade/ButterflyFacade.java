package com.paypal.butterfly.facade;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;

import java.io.File;
import java.util.List;

/**
 * Butterfly façade
 *
 * @author facarvalho
 */
public interface ButterflyFacade {

    List<Extension> getRegisteredExtensions();

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     */
    void transform(File applicationFolder, String templateClassName) throws ButterflyException;

    /**
     * Transform an application, and also accept an additional
     * parameter with configuration
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     * @param configuration Butterfly configuration object
     */
    void transform(File applicationFolder, String templateClassName, Configuration configuration) throws ButterflyException;

    /**
     * Transform an application, and also accept an additional
     * parameter with configuration
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @param configuration Butterfly configuration object
     */
    void transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, Configuration configuration) throws ButterflyException;

}