package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A Butterfly third-party extension. It provides custom
 * transformation templates and validations
 *
 * @author facarvalho
 */
public abstract class Extension<E> {

    private static final Logger logger = LoggerFactory.getLogger(Extension.class);

    private List<Class<? extends TransformationTemplate>> templateClasses = new ArrayList<>();

    /**
     * Adds a new transformation template class to the set
     *
     * @param templateClass the transformation template class to be added to the extension
     *
     * @return this extension
     */
    protected final E add(Class<? extends TransformationTemplate> templateClass) {
        templateClasses.add(templateClass);
        return (E) this;
    }

    /**
     * Returns the extension description
     *
     * @return the extension description
     */
    public abstract String getDescription();

    /**
     * Returns the extension version
     *
     * @return the extension version
     */
    public abstract String getVersion();

    /**
     * Returns a read-only set containing all transformation template classes
     *
     * @return a read-only set containing all transformation template classes
     */
    public final List<Class<? extends TransformationTemplate>> getTemplateClasses() {
        return Collections.unmodifiableList(templateClasses);
    }

    /**
     * Butterfly might be able to automatically identify the type of application
     * and which transformation template to be applied to it. This automatic
     * transformation template resolution is actually performed by each registered
     * Extension class. Based on the application folder, and its content, each
     * registered extension might decide which transformation template should be used
     * to transform it. These are the possible resolution results:
     * <ol>
     *     <li>Empty optional is returned: if application type is not recognized as a known and supported type by the extension</li>
     *     <li>An optional with {@link TransformationTemplate} class is returned: if application type is recognized and application is valid</li>
     *     <li>A {@link TemplateResolutionException} exception is thrown: if the application type is recognized as a known and supported type
     *     (based on most of its folders and files structure and content), however, the extension identifies it as invalid for specific reasons,
     *     (for missing a required file for example, having an invalid property version, etc). Call {@link TemplateResolutionException#getMessage()} for details.
     * </ol>
     * <br>
     * Notice the difference between "not recognized" and "invalid" can be vague and arbitrary.
     * It is entirely up to the extension to define its own criteria and communicate it with the application owners.
     *
     * @param applicationFolder the folder where the code of the application to be transformed is
     * @return see above
     * @throws TemplateResolutionException see above
     */
    public Optional<Class<? extends TransformationTemplate>> automaticResolution(File applicationFolder) throws TemplateResolutionException {
        return Optional.empty();
    }

    /**
     * This is a convenience method in case the Extension subclass wants to implement its
     * {@link #automaticResolution(File)} method based on one or more Maven pom files
     *
     * @param folder the folder where the pom.xml file would be
     * @return the Model object related to the pom.xml file under {@code folder}
     * @throws IOException if pom file does not exist, or any error happens when trying to read it
     * @throws XmlPullParserException if any error happens when trying to parse the pom file
     */
    protected Model getRootPomFile(File folder) throws IOException, XmlPullParserException {
        FileInputStream fileInputStream = null;
        Model model;

        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            File pomFile = new File(folder, "pom.xml");
            fileInputStream = new FileInputStream(pomFile);
            model = reader.read(fileInputStream);
        } finally {
            if (fileInputStream != null) try {
                fileInputStream.close();
            } catch (IOException e) {
                logger.error("Error happened when trying to close file", e);
            }
        }

        return model;
    }

    @Override
    public final String toString() {
        return getClass().getName();
    }

}
