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
     * Butterfly might be able to automatically identify, based on the application
     * content, the most applicable transformation template to transform it.
     * If no template applies to the application content, a {@link TemplateResolutionException}
     * is thrown explaining the reason why no template could be chosen.
     *
     * @param applicationFolder the folder where the code of the application to be transformed is
     * @return the chosen transformation template
     * @throws TemplateResolutionException if no template applies
     */
    public Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException {
        throw new TemplateResolutionException("No transformation template could be resolved");
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
