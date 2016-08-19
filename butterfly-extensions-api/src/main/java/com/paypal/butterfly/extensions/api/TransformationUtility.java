package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationOperationException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
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
 * </br>
 * Also, every TransformationUtility subclass must override {@link #clone()} and every utility
 * specific property defined in the subclass must be copied from the original
 * object to the clone object. Properties inherited from this class and its super classes
 * MUST NOT be copied from original object to cloned object, since that is all already taken
 * care of properly by the framework. Notice that name, parent and path (absolute and relative)
 * are NECESSARILY NOT assigned to the clone object
 *
 * @author facarvalho
 */
public abstract class TransformationUtility<TU, RT> implements Cloneable {

    private static final Logger logger = LoggerFactory.getLogger(TransformationUtility.class);

    private static final String UTILITY_NAME_SYNTAX = "%s-%d-%s";

    // The execution order for this utility on its parent
    // -1 means it has not been registered to any parent yet
    // 1 means first
    private int order = -1;

    public int getOrder() {
        return order;
    }

    // The parent this utility instance has been registered to
    private TransformationUtilityParent parent;

    // This transformation utility instance name
    private String name;

    // Relative path from the application root folder to the file or
    // folder the transformation utility should perform against
    // Setting it to "", or ".", means it will
    // point to the project root folder
    private String relativePath = "";

    // Absolute path to the file or folder the transformation utility
    // should perform against
    private File absoluteFile = null;

    // Holds the name of the context attribute whose value will be set as
    // the absolute file right before execution. If this is null, the
    // actual value in relativePath will be honored
    /** see {@link #absolute(String)} **/
    private String absoluteFileFromContextAttribute = null;

    // An additional relative path to be added to the absolute file
    // coming from the transformation context
    /** see {@link #absolute(String, String)} **/
    private String additionalRelativePath = null;

    // The name to be used as key for the result of this utility
    // when saved into the transformation context.
    // If it is null, then the utility name will be used instead
    private String contextAttributeName = null;

    // Map of properties to be set later, during transformation time.
    // The keys must be utility Java bean property names, and the values
    // must be transformation context attribute names
    private Map<String, String> latePropertiesAttributes = new HashMap<String, String>();

    // Map of properties to be set later, during transformation time.
    // The keys must be utility Java bean property names, and the values
    // must be the setter methods
    private Map<String, Method> latePropertiesSetters = new HashMap<String, Method>();

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
     * time it is added to a parent.
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
     * Register this utility to its parent, and also assign it a name
     * based on the parent name and order of execution.
     * </br>
     * Usually the parent is a {@link TransformationTemplate}
     *
     * @param parent
     * @param order
     * @return this transformation utility
     */
    public final TU setParent(TransformationUtilityParent parent, int order) {
        this.parent = parent;
        this.order = order;

        if(name == null) {
            setName(String.format(UTILITY_NAME_SYNTAX, parent.getName(), order, ((TU) this).getClass().getSimpleName()));
        }

        return (TU) this;
    }

    /**
     * Returns the transformation utility parent
     *
     * @return  the transformation utility parent
     */
    public TransformationUtilityParent getParent() {
        return parent;
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
     * The path separator is automatically normalized, so there are three valid
     * options when separating folders in the path:
     * <ol>
     *  <li>File.separatorChar (e.g. relative("myFolder" + File.separator + "file.txt")</li>
     *  <li>Forward slash (e.g. relative("myFolder/file.txt")</li>
     *  <li>Two backward slashes (e.g. relative("myFolder\\file.txt")</li>
     * </ol>
     * The slashes are replaced by OS specific separator char in runtime.
     * </br>
     * <strong>The default value is ".". which means the root of the transformed application
     </strong>
     *
     * @param relativePath from the application root folder
     *  to the file or folder the transformation utility should be performed against
     * @return this transformation utility
     */
    public final TU relative(String relativePath) {
        this.relativePath = normalizeRelativePathSeparator(relativePath);

        return (TU) this;
    }

    /*
     * Returns a relative path that is in compliance with the current OS in terms of file separator
     */
    private static String normalizeRelativePathSeparator(String _relativePath) {
        if(_relativePath != null) {
            _relativePath = _relativePath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        }

        return _relativePath;
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
    protected final File getAbsoluteFile(File transformedAppFolder, TransformationContext transformationContext) throws TransformationUtilityException {
        if(absoluteFile == null) {
            setAbsoluteFile(transformedAppFolder, transformationContext);
        }

        return absoluteFile;
    }

    private void setAbsoluteFile(File transformedAppFolder, TransformationContext transformationContext) throws TransformationUtilityException {
        if(absoluteFileFromContextAttribute != null) {
            absoluteFile = (File) transformationContext.get(absoluteFileFromContextAttribute);
            if(absoluteFile == null) {
                String exceptionMessage = String.format("Context attribute %s, which is supposed to define absolute file for %s, is null", absoluteFileFromContextAttribute, name);
                TransformationUtilityException exception = new  TransformationUtilityException(exceptionMessage);
                throw exception;
            }
            if(additionalRelativePath != null) {
                absoluteFile = new File(absoluteFile, additionalRelativePath);
                logger.debug("Setting absolute file for {} from context attribute {}, whose value is {}", name, absoluteFileFromContextAttribute, absoluteFile.getAbsolutePath());
            } else {
                logger.debug("Setting absolute file for {} from context attribute {} and additionalRelativePath", name, absoluteFileFromContextAttribute);
            }

            setRelativePath(transformedAppFolder, absoluteFile);

            logger.debug("Relative path for {} has just been reset to {}", name, relativePath);
        } else {
            if (relativePath == null) {
                String exceptionMessage = String.format("Neither absolute nor relative path has been set for transformation utility %s", name);
                TransformationUtilityException exception = new  TransformationUtilityException(exceptionMessage);
                throw exception;
            }
            absoluteFile = new File(transformedAppFolder, relativePath);
        }
    }

    /*
     * Set the relativePath during transformation time, knowing the transformed
     * application folder, and already knowing the absolute file
     */
    private void setRelativePath(File transformedAppFolder, File absoluteFile) {
        relativePath = getRelativePath(transformedAppFolder, absoluteFile);
    }

    /**
     * Returns a relative path from {@code baselineFile} to {@code targetFile}.
     * The file separator used is specific to the current OS. If the baseline file
     * is not entirely within the path to target file, then the target file
     * absolute path is returned
     *
     * @param baselineFile the file whose returned relative path should start from.
     *                     It must be aa direct or indirect parent file to {@code targetFile}
     * @param targetFile the file whose returned relative path should take to
     *
     * @return a relative path from {@code baselineFile} to {@code targetFile}
     */
    public static String getRelativePath(File baselineFile, File targetFile) {
        String baselineAbsolutePath = baselineFile.getAbsolutePath();
        String targetAbsolutePath = targetFile.getAbsolutePath();
        if (!targetAbsolutePath.startsWith(baselineAbsolutePath)) {
            return targetAbsolutePath;
        }
        int beginning = baselineAbsolutePath.length();
        int end = targetAbsolutePath.length();

        return targetAbsolutePath.substring(beginning, end);
    }

    /**
     * This method allows setting properties in this transformation
     * utility during transformation time, right before its execution.
     * This is very useful when the property value is not known during
     * transformation definition. Any attribute stored in the
     * transformation context can be used as the value to be set to the
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
     * @return this transformation utility
     */
    public final TU set(String propertyName, String contextAttributeName) {
        Method method = getMethod(propertyName);
        latePropertiesAttributes.put(propertyName, contextAttributeName);
        latePropertiesSetters.put(propertyName, method);

        return (TU) this;
    }

    private Method getMethod(String propertyName) {
        String methodName = getMethodName(propertyName);
        Class clazz = ((TU) this).getClass();

        for(Method method : clazz.getMethods()) {
            if(method.getName().equals(methodName)) {
                return method;
            }
        }

        String exceptionMessage = String.format("%s is not a valid property", propertyName);
        TransformationDefinitionException e = new TransformationDefinitionException(exceptionMessage);
        logger.error(exceptionMessage, e);

        throw e;
    }

    /**
     * Applies transformation utility properties during transformation time, but
     * prior to execution (right before it). The properties values are gotten from
     * the transformation context object.
     *
     * @param transformationContext
     */
    protected final void applyPropertiesFromContext(TransformationContext transformationContext) throws TransformationUtilityException {
        String attributeName;
        Method method;
        for (final Iterator itr = latePropertiesAttributes.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<String,String> entry = (Map.Entry)itr.next();
            String propertyName = entry.getKey();
            attributeName = latePropertiesAttributes.get(propertyName);
            try {
                method = latePropertiesSetters.get(propertyName);
                Object value = transformationContext.get(attributeName);
                method.invoke(this, value);
            } catch (Exception e) {
                String exceptionMessage = String.format("An error happened when setting property %s from context attribute %s in %s", propertyName, attributeName, name);
                logger.error(exceptionMessage, e);
                throw new TransformationUtilityException(exceptionMessage, e);
            }
        }
    }

    private String getMethodName(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }

    /**
     * There are two ways to specify the file, or folder, the transformation
     * utility is suppose to perform against. The default and most commons one is
     * by setting the relative path to it, which is done usually via the constructor
     * or {@link #relative(String)}). That should be the chosen option whenever
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
     * @see {@link #relative(String)}
     * @see {@link #getRelativePath()}
     */
    public TU absolute(String contextAttributeName) {
        absoluteFileFromContextAttribute = contextAttributeName;
        return (TU) this;
    }

    /**
     * Same as {@link #absolute(String, String)}, however, the absolute
     * file is set with an additional relative path, which is defined via parameter
     * {@code additionalRelativePath}. This method is powerful because it allows setting
     * the absolute file using a portion of the location (absolute) that is only known during
     * transformation time, plus also a second portion of the location (relative) that is
     * already known during definition time
     *
     * @see {@link #absolute(String, String)}
     *
     * @param contextAttributeName the name of the transformation context attribute whose
     *                             value will be set as the absolute file right before
     *                             execution
     * @param additionalRelativePath an additional relative path to be added to the absolute
     *                               file coming from the transformation context. The path
     *                               separator will be normalized, similar to what happens
     *                               in {@link #relative(String)}
     * @return this transformation utility
     * @see {@link #getAbsoluteFile(File, TransformationContext)}
     * @see {@link #relative(String)}
     * @see {@link #getRelativePath()}
     */
    public TU absolute(String contextAttributeName, String additionalRelativePath) {
        absoluteFileFromContextAttribute = contextAttributeName;
        this.additionalRelativePath = normalizeRelativePathSeparator(additionalRelativePath);

        return (TU) this;
    }

    final String getAbsoluteFileFromContextAttribute() {
        return absoluteFileFromContextAttribute;
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

    @Override
    public TransformationUtility<TU, RT> clone() throws CloneNotSupportedException {
        TransformationUtility<TU, RT> clone = (TransformationUtility<TU, RT>) super.clone();

        // Properties we do NOT want to be in the clone (they are being initialized)
        clone.order = -1;
        clone.parent = null;
        clone.name = null;
        clone.relativePath = "";
        clone.absoluteFile = null;
        clone.absoluteFileFromContextAttribute = null;
        clone.additionalRelativePath = null;
        clone.contextAttributeName = null;

        // Properties we want to be in the clone (they are being copied from original object)
        clone.latePropertiesAttributes.putAll(this.latePropertiesAttributes);
        clone.latePropertiesSetters.putAll(this.latePropertiesSetters);

        return clone;
    }

}
