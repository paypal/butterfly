package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Transformation utilities are executed against the to be transformed project,
 * based on the absolute project root folder defined in runtime, and a relative
 * path to a target file or folder, defined in compilation time.
 * </br>
 * Transformation utilities MUST NOT apply any modification to the project though.
 * They are meant instead to only gather information about the project.
 * </br>
 * An example of a transformation operation utility would be to find recursively
 * a particular file based on its name and from a particular location (which would
 * be relative to the project root folder)
 *
 * @see {@link TransformationOperation} for a specialized transformation utility that
 * does modify the project
 *
 * IMPORTANT:
 * Every TransformationUtility subclass MUST be a Java bean, which means they must have
 * a public no arguments default constructor, and also public setters and getters for all
 * their properties. In addition to that, every setter must return the
 * TransformationUtility instance.
 *
 * @author facarvalho
 */
public abstract class TransformationUtility<TU, RT> {

    private static final Logger logger = LoggerFactory.getLogger(TransformationUtility.class);

    private static final String UTILITY_NAME_SYNTAX = "%s-%d-%s";

    // The execution order for this utility on its template
    // -1 means it has not been registered to any template yet
    // 1 means first
    private int order = -1;

    // The template this utility instance has been registered to
    private TransformationTemplate template;

    // This transformation utility instance name
    private String name;

    // Relative path from the application root folder to the file or
    // folder the transformation utility should perform against
    // Setting it to a blank String, like below, means it will
    // point to the project root folder
    private String relativePath = "";

    // Absolute path to the file or folder the transformation utility
    // should perform against
    private File absoluteFile;

    // Holds the name of the context attribute whose value will be set as
    // the absolute file right before execution. If this is null, the
    // actual value in relativePath will be honored
    /** see {@link #setAbsoluteFile(String)} **/
    private String absoluteFileFromContextAttribute = null;

    // An additional relative path to be added to the absolute file
    // coming from the transformation context
    /** see {@link #setAbsoluteFile(String, String)} **/
    private String additionalRelativePath = null;

    // The name to be used as key for the result of this utility
    // when saved into the transformation context.
    // If it is null, then the utility name will be used instead
    private String contextAttributeName = null;

    // Map of properties to be set later, during transformation time.
    // The keys must be utility Java bean property names, and the values
    // must be transformation context attribute names
    private Map<String, String> lateProperties = new HashMap<String, String>();

    /**
     * The public default constructor should always be available by any transformation
     * utility because in many cases all of its properties will be set during
     * transformation time, using the transformation context
     */
    public TransformationUtility() {
    }

    /**
     * Set this transformation utility instance name.
     * If not set, a default name will be assigned at the
     * time it is added to a template.
     *
     * @param name
     * @return this transformation utility
     */
    public TU setName(String name) {
        this.name = name;
        return (TU) this;
    }

    public final String getName() {
        return name;
    }

    /**
     * Set the name to be used as key for the result of this utility
     * when saved into the transformation context.
     * If this is not set, or null, then the utility name will be used instead
     *
     * @param contextAttributeName the name to be used as key for the result of this utility
     * when saved into the transformation context.
     * @return this transformation utility
     */
    public TU setContextAttributeName(String contextAttributeName) {
        this.contextAttributeName = contextAttributeName;
        return (TU) this;
    }

    /**
     * Return the name to be used as key for the result of this utility
     * when saved into the transformation context.
     * If it is null, then the utility name will be used instead
     *
     * @return the name to be used as key for the result of this utility
     * when saved into the transformation context
     */
    public String getContextAttributeName() {
        return contextAttributeName;
    }

    /**
     * Register this utility to a template, and also assign it a name
     * based on the template name and order of execution
     *
     * @param template
     * @param order
     * @return this transformation utility
     */
    final TU setTemplate(TransformationTemplate template, int order) {
        this.template = template;
        this.order = order;

        if(name == null) {
            setName(String.format(UTILITY_NAME_SYNTAX, template.getName(), order, ((TU) this).getClass().getSimpleName()));
        }

        return (TU) this;
    }

    /**
     * Returns the transformation template this utility instance belongs to
     *
     * @return the transformation template this utilityn instance belongs to
     */
    public TransformationTemplate getTemplate() {
        return template;
    }

    /**
     * Returns a short one line, but SPECIFIC, description about the transformation
     * utility, including mentioning the files and/or folders
     * to be manipulated. This is supposed to be an one line statement about the
     * specific transformation utility that was executed. This would be used for example in
     * log statements or user interfaces.
     *
     * @return a short one line, but specific, description about the transformation
     * utility
     */
    public abstract String getDescription();

    /**
     * Sets the relative path from the application root folder
     * to the file or folder the transformation utility should perform against.
     * Three options are valid when separating folders in the path:
     * <ol>
     * <li>1-File.separatorChar (e.g. setRelativePath("myFolder" + File.separator + "file.txt")</li>
     * <li>2-Forward slash (e.g. setRelativePath("myFolder/file.txt")</li>
     * <li>3-Two backward slashes (e.g. setRelativePath("myFolder\\file.txt")</li>
     * </ol>
     * The slashes are replaced by OS specific separator char in runtime.
     *
     * @param relativePath from the application root folder
     *  to the file or folder the transformation utility should be performed against
     * @return this transformation utility
     */
    public final TU setRelativePath(String relativePath) {
        // It is ok to be null, in case it will be set during transformation time
        if(relativePath != null) {
            this.relativePath = relativePath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        }

        return (TU) this;
    }

    /**
     * Returns relative path (from the application root folder) to the
     * file or folder the transformation utility is suppose to perform against
     *
     * @return relative path (from the application root folder) to the
     * file or folder the transformation utility is suppose to perform against
     */
    protected final String getRelativePath() {
        return relativePath;
    }

    /**
     * Returns an absolute path to the file or folder the transformation
     * utility is suppose to perform against
     *
     * @param transformedAppFolder
     * @return an absolute path to the file or folder the transformation
     * utility is suppose to perform against
     */
    protected final File getAbsoluteFile(File transformedAppFolder, TransformationContext transformationContext) {
        if(absoluteFile == null) {
            setAbsoluteFile(transformedAppFolder, transformationContext);
        }

        return absoluteFile;
    }

    private void setAbsoluteFile(File transformedAppFolder, TransformationContext transformationContext) {
        if(absoluteFileFromContextAttribute != null) {
            absoluteFile = (File) transformationContext.get(absoluteFileFromContextAttribute);
            if(additionalRelativePath != null) {
                absoluteFile = new File(absoluteFile, additionalRelativePath);
                logger.debug("Setting absolute file for {} from context attribute {}, whose value is {}", name, absoluteFileFromContextAttribute, absoluteFile.getAbsolutePath());
            } else {
                logger.debug("Setting absolute file for {} from context attribute {} and additionalRelativePath", name, absoluteFileFromContextAttribute);
            }

            setRelativePath(transformedAppFolder, absoluteFile);

            logger.debug("Relative path for {} has just been reset to {}", name, relativePath);
        } else {
            absoluteFile = new File(transformedAppFolder, getRelativePath());
        }
    }

    /*
     * Set the relativePath during transformation time, knowing the transformed
     * application folder, and already knowing the absolute file
     */
    private void setRelativePath(File transformedAppFolder, File absoluteFile) {
        int beginning = transformedAppFolder.getAbsolutePath().length();
        int end = absoluteFile.getAbsolutePath().length();
        relativePath = absoluteFile.getAbsolutePath().substring(beginning, end);
    }

    /**
     * This method allows setting properties in this transformation
     * utility during transformation time, right before its execution.
     * This is very useful when the property value is not known during
     * transformation definition. Any other attribute stored in the
     * transformation context, can be used as the value to be set to the
     * property. In most of the cases the result of a prior
     * transformation utility is used as property value.
     * Notice that, because this feature relies on reflection, it is not
     * cheap, especially because it happens during transformation time.
     * So, use it only when really necessary.
     *
     * @param propertyName the transformation utility Java bean property name
     * @param contextAttributeName the name of the transformation context attribute whose
     *                             value will be set as the property value right before
     *                             execution
     * TODO what about exception handling? checked or not? definition or transformation time?
     * @return this transformation utility
     */
    public final TU setPropertyFromContext(String propertyName, String contextAttributeName) {
        lateProperties.put(propertyName, contextAttributeName);

        return (TU) this;
    }

    /**
     * Applies transformation utility properties during transformation time, but
     * prior to execution (right before it). The properties values are gotten from
     * the transformation context object.
     *
     * @param transformationContext
     */
    protected final void applyPropertiesFromContext(TransformationContext transformationContext) {
        // TODO what about exception handling? checked or not? definition or transformation time?
        // TODO take care of all other properties via reflections
    }

    /**
     * There are two ways to specify the file, or folder, the transformation
     * utility is suppose to perform against. The default and most commons one is
     * by setting the relative path to it, which is done usually via the constructor
     * or {@link #setRelativePath(String)}). That should be the chosen option whenever
     * the relative location is known during transformation template definition time.
     * </br>
     * However, sometimes that is not possible because that location will only be known
     * during transformation time. In cases like this, usually another utility is used to
     * find that location first, and then save it as transformation context attribute. In
     * this case, this setter here can be used to set the absolute file location based
     * on such context attribute. Whenever this is set, the relative path attribute is
     * ignored.
     *
     * @param contextAttributeName the name of the transformation context attribute whose
     *                             value will be set as the absolute file right before
     *                             execution
     * @return this transformation utility
     * @see {@link #getAbsoluteFile(File, TransformationContext)}
     * @see {@link #setRelativePath(String)}
     * @see {@link #getRelativePath()}
     */
    public TU setAbsoluteFile(String contextAttributeName) {
        absoluteFileFromContextAttribute = contextAttributeName;
        return (TU) this;
    }

    /**
     * Same as {@link #setAbsoluteFile(String, String)}, however, the absolute
     * file is set with an additional relative path, which is defined via parameter
     * {@code additionalRelativePath}. This method is powerful because it allows setting
     * the absolute file using a portion of the location (absolute) that is only known during
     * transformation time, plus also a second portion of the location (relative) that is
     * already known during definition time
     *
     * @see {@link #setAbsoluteFile(String, String)}
     *
     * @param contextAttributeName the name of the transformation context attribute whose
     *                             value will be set as the absolute file right before
     *                             execution
     * @param additionalRelativePath an additional relative path to be added to the absolute
     *                               file coming from the transformation context
     * @return this transformation utility
     * @see {@link #getAbsoluteFile(File, TransformationContext)}
     * @see {@link #setRelativePath(String)}
     * @see {@link #getRelativePath()}
     */
    public TU setAbsoluteFile(String contextAttributeName, String additionalRelativePath) {
        absoluteFileFromContextAttribute = contextAttributeName;
        this.additionalRelativePath = additionalRelativePath;

        return (TU) this;
    }

    /**
     * Performs the transformation utility against
     * the application to be transformed
     * </br>
     * This is the one called by the transformation
     * engine, and regardless of any customization it
     * could have, it must always:
     * <ol>
     *     <li>1- Call {@link #applyPropertiesFromContext(TransformationContext)}</li>
     *     <li>2- Call {@link #execution(File, TransformationContext)}</li>
     * </ol>
     * </br>
     * This method is NOT supposed to be overwritten,
     * unless you really know what you are doing.
     *
     * @param transformedAppFolder
     * @param transformationContext
     *
     * @return the result
     */
    public synchronized RT perform(File transformedAppFolder, TransformationContext transformationContext) throws TransformationUtilityException {
        applyPropertiesFromContext(transformationContext);

        RT result;
        try {
            result = execution(transformedAppFolder, transformationContext);
        } catch(Exception e) {
            throw new TransformationOperationException(getName() + " has failed", e);
        }

        return result;
    }

    /**
     * The implementation of this transformation utility.
     * The returned object is the result of the execution and is always
     * automatically saved in the transformation context as a new
     * attribute, whose key is the name of the transformation utility.
     *
     * @param transformedAppFolder
     *
     * @return an object with the result of this execution, to be better defined
     * by the concrete utility class, since its type is generic
     */
    protected abstract RT execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception;

    @Override
    public String toString() {
        return getDescription();
    }

}
