package com.paypal.butterfly.api;

import java.io.File;
import java.util.List;
import java.util.Properties;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;

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
     * Returns an unmodifiable list of all registered extensions
     *
     * @return an unmodifiable list of all registered extensions
     */
    List<Extension> getExtensions();

    /**
     * Butterfly might be able to automatically identify the type of application
     * and which transformation template to be applied to it. This automatic
     * transformation template resolution is performed by each registered
     * Extension class. Based on the application folder, and its content, each
     * registered extension might decide which transformation template should be used
     * to transform it. Only one can be chosen. If no template applies,
     * if more than one applies (from different extensions), or if no
     * extension has been registered, a {@link TemplateResolutionException} is
     * thrown explaining the reason why no template could be chosen.
     *
     * @param applicationFolder the folder where the code of the application to be transformed is
     * @return the chosen transformation template
     * @throws TemplateResolutionException if no template could be resolved, more than one was resolved
     * (from different extensions), or if no extension has been registered
     */
    Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException;

    /**
     * Creates and returns a new {@link Configuration} object
     * set to apply the transformation against the original application folder
     * and the result will not be compressed to a zip file.
     * <br>
     * Notice that calling this method will result in {@link Configuration#isModifyOriginalFolder()}
     * to return {@code true}.
     *
     * @param properties a properties object specifying details about the transformation itself.
     *                   These properties help to specialize the
     *                   transformation, for example, determining if certain operations should
     *                   be skipped or not, or how certain aspects of the transformation should
     *                   be executed. The set of possible properties is defined by the used transformation
     *                   extension and template, read the documentation offered by the extension
     *                   for further details. The properties values are defined by the user requesting the transformation.
     *                   Properties are optional, so, if not desired, this parameter can be set to null.
     * @return a brand new {@link Configuration} object
     */
    Configuration newConfiguration(Properties properties);

    /**
     * Creates and returns a new {@link Configuration} object
     * set to place the transformed application at a new folder at the original application
     * parent folder, besides compressing it to a zip file, depending on {@code zipOutput}.
     * <br>
     * The transformed application folder's name is the same as original folder,
     * plus a "-transformed-yyyyMMddHHmmssSSS" suffix.
     * <br>
     * Notice that calling this method will result in {@link Configuration#isModifyOriginalFolder()}
     * to return {@code false}.
     *
     * @param properties a properties object specifying details about the transformation itself.
     *                   These properties help to specialize the
     *                   transformation, for example, determining if certain operations should
     *                   be skipped or not, or how certain aspects of the transformation should
     *                   be executed. The set of possible properties is defined by the used transformation
     *                   extension and template, read the documentation offered by the extension
     *                   for further details. The properties values are defined by the user requesting the transformation.
     *                   Properties are optional, so, if not desired, this parameter can be set to null.
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     * @return a brand new {@link Configuration} object
     */
    Configuration newConfiguration(Properties properties, boolean zipOutput);

    /**
     * Creates and returns a new {@link Configuration} object
     * set to place the transformed application at {@code outputFolder},
     * and compress it to a zip file or not, depending on {@code zipOutput}.
     * <br>
     * Notice that calling this method will result in {@link Configuration#isModifyOriginalFolder()}
     * to return {@code false}.
     *
     * @param properties a properties object specifying details about the transformation itself.
     *                   These properties help to specialize the
     *                   transformation, for example, determining if certain operations should
     *                   be skipped or not, or how certain aspects of the transformation should
     *                   be executed. The set of possible properties is defined by the used transformation
     *                   extension and template, read the documentation offered by the extension
     *                   for further details. The properties values are defined by the user requesting the transformation.
     *                   Properties are optional, so, if not desired, this parameter can be set to null.
     * @param outputFolder the output folder where the transformed application is
     *                     supposed to be placed
     * @param zipOutput if true, the transformed application folder will be compressed into a zip file
     * @return a brand new {@link Configuration} object
     * @throws IllegalArgumentException if {@code outputFolder} is null, does not exist, or is not a directory
     */
    Configuration newConfiguration(Properties properties, File outputFolder, boolean zipOutput);

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     * @return the transformation result object
     * @throws ButterflyException if the template class could not be found
     */
    TransformationResult transform(File applicationFolder, String templateClassName) throws ButterflyException;

    /**
     * Transform an application, and also accepts an additional
     * parameter with configuration. See {@link Configuration} for further information.
     *
     * @param applicationFolder application folder
     * @param templateClassName transformation template class name
     * @param configuration Butterfly configuration object
     * @return the transformation result object
     * @throws ButterflyException if the template class could not be found
     */
    TransformationResult transform(File applicationFolder, String templateClassName, Configuration configuration) throws ButterflyException;

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @return the transformation result object
     */
    TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass);

    /**
     * Transform an application, and also accepts an additional
     * parameter with configuration. See {@link Configuration} for further information.
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @param configuration Butterfly configuration object
     * @return the transformation result object
     */
    TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, Configuration configuration);

    /**
     * Upgrade an application based on an upgrade path
     *
     * @param applicationFolder application folder
     * @param upgradePath upgrade path object used to upgrade this application
     * @return the transformation result object
     */
    TransformationResult transform(File applicationFolder, UpgradePath upgradePath);

    /**
     * Transform an application based on an upgrade path, and also accepts an additional
     * parameter with configuration. See {@link Configuration} for further information.
     *
     * @param applicationFolder application folder
     * @param upgradePath upgrade path object used to upgrade this application
     * @param configuration Butterfly configuration object
     * @return the transformation result object
     */
    TransformationResult transform(File applicationFolder, UpgradePath upgradePath, Configuration configuration);

}