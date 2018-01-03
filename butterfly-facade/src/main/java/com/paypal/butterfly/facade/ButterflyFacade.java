package com.paypal.butterfly.facade;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;

import java.io.File;

/**
 * Butterfly façade
 *
 * @author facarvalho
 */
public interface ButterflyFacade {

    /**
     * Returns Butterfly version
     *
     * @return Butterfly version
     */
    String getButterflyVersion();

    /**
     * Returns the registered extension, or null, if none has been registered.
     * If multiple extensions have been registered, throw an {@link IllegalStateException}.
     *
     * @throws IllegalStateException if multiple extensions have been registered
     * @return the registered extension
     */
    Extension getRegisteredExtension();

    /**
     * Butterfly might be able to automatically identify the type of application
     * and which transformation template to be applied to it. This automatic
     * transformation template resolution is delegated to the registered
     * Extension class. Based on the application folder, and its content, the
     * registered extension might decide which transformation template should be used
     * to transform it. Only one can be chosen. If no template applies, or if no or multiple
     * extensions have been registered, a {@link TemplateResolutionException} is
     * thrown explaining the reason why no template could be chosen.
     *
     * @param applicationFolder the folder where the code of the application to be transformed is
     * @return the chosen transformation template
     * @throws TemplateResolutionException if no template applies or if no or multiple extension have been registered
     */
    Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException;

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     * @return the transformation result object
     * @throws ButterflyException in case the transformation did not succeed
     */
    TransformationResult transform(File applicationFolder, String templateClassName) throws ButterflyException;

    /**
     * Transform an application, and also accept an additional
     * parameter with configuration
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     * @param configuration Butterfly configuration object
     * @return the transformation result object
     * @throws ButterflyException in case the transformation did not succeed
     */
    TransformationResult transform(File applicationFolder, String templateClassName, Configuration configuration) throws ButterflyException;

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @return the transformation result object
     * @throws ButterflyException in case the transformation did not succeed
     */
    TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass) throws ButterflyException;

    /**
     * Transform an application, and also accept an additional
     * parameter with configuration
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @param configuration Butterfly configuration object
     * @return the transformation result object
     * @throws ButterflyException in case the transformation did not succeed
     */
    TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, Configuration configuration) throws ButterflyException;

    /**
     * Upgrade an application based on an upgrade path
     *
     * @param applicationFolder application folder
     * @param upgradePath upgrade path object used to upgrade this application
     * @return the transformation result object
     * @throws ButterflyException in case the transformation did not succeed
     */
    TransformationResult transform(File applicationFolder, UpgradePath upgradePath) throws ButterflyException;

    /**
     * Transform an application based on an upgrade path, and also accept an additional
     * parameter with configuration
     *
     * @param applicationFolder application folder
     * @param upgradePath upgrade path object used to upgrade this application
     * @param configuration Butterfly configuration object
     * @return the transformation result object
     * @throws ButterflyException in case the transformation did not succeed
     */
    TransformationResult transform(File applicationFolder, UpgradePath upgradePath, Configuration configuration) throws ButterflyException;

}