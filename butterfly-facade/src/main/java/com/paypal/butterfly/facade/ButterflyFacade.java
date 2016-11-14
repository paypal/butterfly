package com.paypal.butterfly.facade;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.facade.exception.TemplateResolutionException;

import java.io.File;
import java.util.List;

/**
 * Butterfly façade
 *
 * @author facarvalho
 */
public interface ButterflyFacade {

    /**
     * Returns an unmodifiable list of all registered extensions
     *
     * @return an unmodifiable list of all registered extensions
     */
    List<Extension> getRegisteredExtensions();

    /**
     * Butterfly might be able to automatically identify the type of application
     * and which transformation template to be applied to it. This automatic
     * transformation template resolution is actually performed by each registered
     * Extension class. Based on the application folder, and its content, each
     * registered extension might decide which transformation template should be used
     * to transform it. Only one or none can be chosen. If no one applies, null is
     * returned. If more than one applies, coming from different extensions, then
     * a {@link com.paypal.butterfly.extensions.api.exception.ButterflyRuntimeException}
     * exception is thrown
     *
     * @param applicationFolder the folder where the code of the application to be transformed is
     * @return the chosen transformation template, or null, if no one applies
     * @throws TemplateResolutionException thrown if more than one transformation template is
     *          identified as applicable to the application
     */
    Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException;

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     * @return the transformed application folder
     */
    File transform(File applicationFolder, String templateClassName) throws ButterflyException;

    /**
     * Transform an application, and also accept an additional
     * parameter with configuration
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     * @param configuration Butterfly configuration object
     * @return the transformed application folder
     */
    File transform(File applicationFolder, String templateClassName, Configuration configuration) throws ButterflyException;

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @return the transformed application folder
     */
    File transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass) throws ButterflyException;

    /**
     * Transform an application, and also accept an additional
     * parameter with configuration
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @param configuration Butterfly configuration object
     * @return the transformed application folder
     */
    File transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, Configuration configuration) throws ButterflyException;

    /**
     * Upgrade an application based on an upgrade path
     *
     * @param applicationFolder application folder
     * @param upgradePath upgrade path object used to upgrade this application
     * @return the transformed application folder
     */
    File transform(File applicationFolder, UpgradePath upgradePath) throws ButterflyException;

    /**
     * Transform an application based on an upgrade path, and also accept an additional
     * parameter with configuration
     *
     * @param applicationFolder application folder
     * @param upgradePath upgrade path object used to upgrade this application
     * @param configuration Butterfly configuration object
     * @return the transformed application folder
     */
    File transform(File applicationFolder, UpgradePath upgradePath, Configuration configuration) throws ButterflyException;

}