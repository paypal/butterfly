package com.paypal.butterfly.api;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;

import java.io.File;
import java.util.List;
import java.util.Properties;

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
     * @throws IllegalArgumentException if properties object is invalid. Properties name must
     *                   be non blank and only contain alphabetical characters, dots, underscore or hyphen. Properties
     *                   object must be Strings and cannot be null.
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
     * @throws IllegalArgumentException if properties object is invalid. Properties name must
     *                   be non blank and only contain alphabetical characters, dots, underscore or hyphen. Properties
     *                   object must be Strings and cannot be null.
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
     * @throws IllegalArgumentException if properties object is invalid. Properties name must
     *                   be non blank and only contain alphabetical characters, dots, underscore or hyphen. Properties
     *                   object must be Strings and cannot be null.
     */
    Configuration newConfiguration(Properties properties, File outputFolder, boolean zipOutput);

    /**
     * Transforms an application. If <code>templateClass</code> is a {@link UpgradeStep},
     * application will be upgraded to the latest version.
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @return the transformation result object
     */
    TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass);

    /**
     * Transforms an application, and also accepts an additional
     * parameter with configuration. See {@link Configuration} for further information.
     * If <code>templateClass</code> is a {@link UpgradeStep}, application will be upgraded according to <code>version</code>.
     *
     * @param applicationFolder application folder
     * @param templateClass transformation template class
     * @param version the target upgrade version. If this parameter is null or blank, application will be upgraded to the latest version.
     *                If <code>templateClass</code> is not a {@link UpgradeStep}, this parameter is ignored.
     * @param configuration Butterfly configuration object
     * @throws IllegalArgumentException if <code>templateClass</code> is a {@link UpgradeStep} and <code>version</code> is not empty and an unknown version
     * @return the transformation result object
     */
    TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, String version, Configuration configuration);

}