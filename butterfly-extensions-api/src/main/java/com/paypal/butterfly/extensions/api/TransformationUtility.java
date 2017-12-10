package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Gathers information about the project to be transformed without applying any modification on it.
 * It is the key element of Butterfly transformation engine. The result information is saved in the
 * {@link TransformationContext} object, to be used later by other transformation utilities.
 * <br>
 * Transformation utilities are executed against the to be transformed project,
 * based on the absolute project root folder defined in runtime, and a relative
 * path to a target file or folder, defined in compilation time.
 * <br>
 * Transformation utilities are also known by {@code TU}.
 * <br>
 * An example of a transformation operation utility would be to find recursively
 * a particular file based on its name and from a particular location (which would
 * be relative to the project root folder)
 *
 * See {@link TransformationOperation} for a specialized transformation utility that
 * does modify the project
 *
 * IMPORTANT:
 * Every TransformationUtility subclass MUST have a public no arguments default constructor,
 * and also public setters and getters for all properties they want to expose via {@link #set(String, String)}.
 * In addition to that, every setter must return the TransformationUtility instance.
 * <br>
 * Also, every TransformationUtility subclass must override {@link #clone()} and every utility
 * specific property defined in the subclass must be copied from the original
 * object to the clone object. Properties inherited from this class and its super classes
 * MUST NOT be copied from original object to cloned object, since that is all already taken
 * care of properly by the framework. Notice that name, parent and path (absolute and relative)
 * are NECESSARILY NOT assigned to the clone object
 *
 * @author facarvalho
 */
// TODO create another type to be parent of TO and TU, this way the result type will be better organized
// How to name it? transformation node?
// This type should be the one to be added to a template
public abstract class TransformationUtility<T extends TransformationUtility> implements Cloneable {

    private static final Logger logger = LoggerFactory.getLogger(TransformationUtility.class);

    protected static final String UTILITY_NAME_SYNTAX = "%s-%d-%s";

    // The execution order for this utility on its parent
    // -1 means it has not been registered to any parent yet
    // 1 means first
    private int order = -1;

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
    // The keys must be utility Java property names, and the values
    // must be transformation context attribute names
    private Map<String, String> latePropertiesAttributes = new HashMap<>();

    // Map of properties to be set later, during transformation time.
    // The keys must be utility Java property names, and the values
    // must be the setter methods
    private Map<String, Method> latePropertiesSetters = new HashMap<>();

    // Abort the whole transformation if this operation fails
    private boolean abortOnFailure = false;

    // A message to be logged if a fail happens and transformation
    // has to be aborted
    private String abortionMessage;

    // See comments in isSaveResult method
    private boolean saveResult = true;

    // See comments in dependsOn method
    private String[] dependencies = null;

    // Optional condition to let this operation be executed (if true)
    // This is the name of a transformation context attribute
    // whose value is a boolean
    private String ifConditionAttributeName = null;

    // Optional condition to let this operation be executed (if false)
    // This is the name of a transformation context attribute
    // whose value is a boolean
    private String unlessConditionAttributeName = null;

    // Optional condition to let this operation be executed (if true)
    // This is the actual UtilityCondition object to be executed
    // right before this TU is executed. Its result is then evaluated
    // and, based on that, this T is executed or not
    private UtilityCondition utilityCondition = null;

    // Indicates whether or not this utility has already been
    // executed. Transformation utilities are supposed to
    // be executed ONLY ONCE. If there is a need to execute
    // it more than once, then it should be cloned before execution,
    // then the original and the clone can be executed. They will
    // have necessarily different names and different result objects
    // in the TCA
    private AtomicBoolean hasBeenPerformed = new AtomicBoolean(false);

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
     * @param name transformation utility instance name
     * @return this transformation utility instance
     */
    protected T setName(String name) {
        if(StringUtils.isBlank(name)) {
            throw new TransformationDefinitionException(name + " cannot be blank");
        }
        this.name = name;
        return (T) this;
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
     * @return this transformation utility instance
     */
    public T setContextAttributeName(String contextAttributeName) {
        this.contextAttributeName = contextAttributeName;
        return (T) this;
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
     * <br>
     * Usually the parent is a {@link TransformationTemplate}
     *
     * @param parent the parent to be set to this utility
     * @param order the order of execution of this utility
     * @return this transformation utility instance
     */
    public final T setParent(TransformationUtilityParent parent, int order) {
        this.parent = parent;
        this.order = order;

        if(name == null) {
            setName(String.format(UTILITY_NAME_SYNTAX, parent.getName(), order, ((T) this).getClass().getSimpleName()));
        }

        return (T) this;
    }

    /**
     * Returns the transformation utility parent
     *
     * @return the transformation utility parent
     */
    public TransformationUtilityParent getParent() {
        return parent;
    }

    /**
     * Returns the transformation template this utility belongs to
     *
     * @return the transformation template this utility belongs to
     */
    public TransformationTemplate getTransformationTemplate() {
        TransformationUtilityParent parent = getParent();
        while (!(parent instanceof TransformationTemplate)) {
            parent = ((TransformationUtility) parent).getParent();
        }
        return (TransformationTemplate) parent;
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
     * Returns the execution order for this utility on its parent.
     * Value -1 means it has not been registered to any parent yet,
     * while 1 means first.
     *
     * @return the execution order for this utility on its parent
     */
    public int getOrder() {
        return order;
    }

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
     * <br>
     * <strong>The default value is ".". which means the root of the transformed application</strong>
     *
     * @param relativePath from the application root folder
     *  to the file or folder the transformation utility should be performed against
     * @return this transformation utility instance
     */
    public final T relative(String relativePath) {
        this.relativePath = normalizeRelativePathSeparator(relativePath);

        return (T) this;
    }

    /*
     * Returns a relative path that is in compliance with the current OS in terms of file separator,
     * or null, if the passed relative path is null
     */
    protected static String normalizeRelativePathSeparator(String relativePath) {
        String normalizedRelativePath = null;
        if(relativePath != null) {
            normalizedRelativePath = relativePath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        }

        return normalizedRelativePath;
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
     * utility is supposed to perform against
     *
     * @param transformedAppFolder the folder where the transformed application code is
     * @param transformationContext the transformation context object
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
                // FIXME a better exception is necessary here for cases when the absolute path transformation context attribute value is null
                throw  new  TransformationUtilityException(exceptionMessage);
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
                throw  new  TransformationUtilityException(exceptionMessage);
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
     * @param propertyName the transformation utility Java property name
     * @param contextAttributeName the name of the transformation context attribute whose
     *                             value will be set as the property value right before
     *                             execution
     * @return this transformation utility instance
     */
    public final T set(String propertyName, String contextAttributeName) {
        Method method = getMethod(propertyName);
        latePropertiesAttributes.put(propertyName, contextAttributeName);
        latePropertiesSetters.put(propertyName, method);

        return (T) this;
    }

    private Method getMethod(String propertyName) {
        String methodName = getMethodName(propertyName);
        Class clazz = ((T) this).getClass();

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
     * @param transformationContext the transformation context object
     */
    protected final void applyPropertiesFromContext(TransformationContext transformationContext) throws TransformationUtilityException {
        String attributeName;
        Method method;
        Object value = null;
        for (final Iterator itr = latePropertiesAttributes.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<String, String> entry = (Map.Entry) itr.next();
            String propertyName = entry.getKey();
            attributeName = latePropertiesAttributes.get(propertyName);
            try {
                method = latePropertiesSetters.get(propertyName);
                value = transformationContext.get(attributeName);

                // Numeric values returned from {@link com.paypal.butterfly.utilities.misc.RunScript} might need to be converted,
                // since Java script and Java data types differ.
                if ((method.getParameterTypes()[0].getTypeName().equals("int") || method.getParameterTypes()[0].getTypeName().equals("Integer")) && value instanceof Long) {
                    value = ((Long) value).intValue();
                    logger.debug("Converting value from Long to int. Value came from {} and is being set for property {} in {}", attributeName, propertyName, name);
                } else if ((method.getParameterTypes()[0].getTypeName().equals("short") || method.getParameterTypes()[0].getTypeName().equals("Short")) && value instanceof Long) {
                    value = ((Long) value).shortValue();
                    logger.debug("Converting value from Long to short. Value came from {} and is being set for property {} in {}", attributeName, propertyName, name);
                }

                method.invoke(this, value);
            } catch (TransformationDefinitionException e) {
                String exceptionMessage = String.format("An error happened when setting property %s from context attribute %s in %s", propertyName, attributeName, name);
                if(value == null) {
                    logger.warn("Attribute %s is NULL. This problem could be avoided by setting the utility that generated it as a dependency for %s", getName());
                }
                throw new TransformationUtilityException(exceptionMessage, e);
            } catch (Exception e) {
                String exceptionMessage = String.format("An error happened when setting property %s from context attribute %s in %s", propertyName, attributeName, name);
                throw new TransformationUtilityException(exceptionMessage, e);
            }
        }
    }

    private String getMethodName(String propertyName) {
        return String.format("set%s%s", propertyName.substring(0, 1).toUpperCase(), propertyName.substring(1));
    }

    /**
     * There are two ways to specify the file, or folder, the transformation
     * utility is suppose to perform against. The default and most commons one is
     * by setting the relative path to it, which is done usually via the constructor
     * or {@link #relative(String)}). That should be the chosen option whenever
     * the relative location is known during transformation template definition time.
     * <br>
     * However, sometimes that is not possible because that location will only be known
     * during transformation time. In cases like this, usually another utility is used to
     * find that location first, and then save it as transformation context attribute. In
     * this case, this setter here can be used to set the absolute file location based
     * on such context attribute. Whenever this is set, the relative path attribute is
     * ignored.
     * <br>
     * See also {@link #getAbsoluteFile(File, TransformationContext)}, {@link #relative(String)}
     * and {@link #getRelativePath()}
     *
     * @param contextAttributeName the name of the transformation context attribute whose
     *                             value will be set as the absolute file right before
     *                             execution
     * @return this transformation utility instance
     */
    public T absolute(String contextAttributeName) {
        absoluteFileFromContextAttribute = contextAttributeName;
        return (T) this;
    }

    /**
     * Same as {@link #absolute(String, String)}, however, the absolute
     * file is set with an additional relative path, which is defined via parameter
     * {@code additionalRelativePath}. This method is powerful because it allows setting
     * the absolute file using a portion of the location (absolute) that is only known during
     * transformation time, plus also a second portion of the location (relative) that is
     * already known during definition time
     * <br>
     * See also {@link #getAbsoluteFile(File, TransformationContext)}, {@link #relative(String)}
     * and {@link #getRelativePath()}
     *
     * @param contextAttributeName the name of the transformation context attribute whose
     *                             value will be set as the absolute file right before
     *                             execution
     * @param additionalRelativePath an additional relative path to be added to the absolute
     *                               file coming from the transformation context. The path
     *                               separator will be normalized, similar to what happens
     *                               in {@link #relative(String)}
     * @return this transformation utility instance
     */
    public T absolute(String contextAttributeName, String additionalRelativePath) {
        absoluteFileFromContextAttribute = contextAttributeName;
        this.additionalRelativePath = normalizeRelativePathSeparator(additionalRelativePath);

        return (T) this;
    }

    final String getAbsoluteFileFromContextAttribute() {
        return absoluteFileFromContextAttribute;
    }

    /**
     * Performs the transformation utility against
     * the application to be transformed
     * <br>
     * This is the one called by the transformation
     * engine, and regardless of any customization it
     * could have, it must always:
     * <ol>
     *     <li>1- Call {@link #applyPropertiesFromContext(TransformationContext)}</li>
     *     <li>2- Call {@link #execution(File, TransformationContext)}</li>
     * </ol>
     * <br>
     * This method is NOT supposed to be overwritten,
     * unless you really know what you are doing.
     *
     * @param transformedAppFolder the folder where the transformed application code is
     * @param transformationContext the transformation context object
     *
     * @return the result
     */
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    public PerformResult perform(File transformedAppFolder, TransformationContext transformationContext) throws TransformationUtilityException {
        if(hasBeenPerformed.get()) {
            String exceptionMessage = String.format("Utility %s has already been performed", getName());
            TransformationUtilityException e = new TransformationUtilityException(exceptionMessage);
            return PerformResult.error(this, e);
        }

        // Checking for IF condition
        if(ifConditionAttributeName != null) {
            Object conditionResult = transformationContext.get(ifConditionAttributeName);
            if (conditionResult == null || conditionResult instanceof Boolean && !((Boolean) conditionResult).booleanValue()) {
                String details = String.format("%s was skipped due to failing 'if' condition: %s", getName(), ifConditionAttributeName);
                return PerformResult.skippedCondition(this, details);
            }
        }

        // Checking for UNLESS condition
        if(unlessConditionAttributeName != null) {
            Object conditionResult = transformationContext.get(unlessConditionAttributeName);
            if (conditionResult != null && conditionResult instanceof Boolean && ((Boolean) conditionResult).booleanValue()) {
                String details = String.format("%s was skipped due to failing 'unless' condition: %s", getName(), unlessConditionAttributeName);
                return PerformResult.skippedCondition(this, details);
            }
        }

        // Checking for UtilityCondition condition
        if(utilityCondition != null) {
            try {
                TransformationUtility utilityCondition = this.utilityCondition.clone();
                utilityCondition.relative(this.getRelativePath());
                TUExecutionResult conditionExecutionResult = (TUExecutionResult) utilityCondition.execution(transformedAppFolder, transformationContext);
                Object conditionResult = conditionExecutionResult.getValue();
                if (conditionResult == null || conditionResult instanceof Boolean && !((Boolean) conditionResult).booleanValue()) {
                    String utilityConditionName = (utilityCondition.getName() == null ? utilityCondition.toString() : utilityCondition.getName());
                    String details = String.format("%s was skipped due to failing UtilityCondition '%s'", getName(), utilityConditionName);
                    return PerformResult.skippedCondition(this, details);
                }
            } catch (CloneNotSupportedException e) {
                String exceptionMessage = String.format("%s can't be executed because the UtilityCondition object associated with it can't be cloned", getName());
                TransformationUtilityException ex = new TransformationUtilityException(exceptionMessage, e);
                return PerformResult.error(this, ex);
            }

        }

        // Checking for dependencies
        PerformResult result = (PerformResult) checkDependencies(transformationContext);
        if (result != null) {
            return result;
        }

        // Applying properties during transformation time
        applyPropertiesFromContext(transformationContext);

        TransformationUtilityException ex = null;

        try {
            ExecutionResult executionResult = execution(transformedAppFolder, transformationContext);
            result = PerformResult.executionResult(this, executionResult);
        } catch(Exception e) {
            String exceptionMessage = String.format("Utility %s has failed", getName());
            ex = new TransformationUtilityException(exceptionMessage, e);
            return PerformResult.error(this, ex);
        } finally {
            // This if and the following below, even though similar, address different execution paths,
            // so they must both be here, do not remove none of them thinking that this is redundant code
            if (result == null && ex == null) {
                String exceptionMessage = String.format("Utility %s has failed and has not produced any exception detailing the failure. This utility code might be defective, or you might be using a non supported JRE (such as Open JDK 1.7).", getName());
                ex = new TransformationUtilityException(exceptionMessage);
                logger.error("", ex);
            }
            hasBeenPerformed.set(true);
        }

        if (result == null) {
            String exceptionMessage = String.format("Utility %s has failed and has not produced any exception detailing the failure. This utility code might be defective, since they must never return null.", getName());
            ex = new TransformationUtilityException(exceptionMessage);
            result = PerformResult.error(this, ex);
        }

        return result;
    }

    /**
     * If set to true, abort the whole transformation if validation or execution fails.
     * If not, just state a warning, aborts the operation execution only.
     * <strong>Notice that abortion here means interrupting the transformation.
     * It does not mean rolling back the changes that have might already been done
     * by this transformation operation by the time it failed</strong>
     *
     * @param abort if set to true, abort the whole transformation if validation or execution fails.
     *              If not, just state a warning, aborts the operation execution only
     * @return this transformation utility instance
     */
    public final T abortOnFailure(boolean abort) {
        abortOnFailure = abort;
        return (T) this;
    }

    /**
     * If set to true, abort the whole transformation if validation or execution fails.
     * If not, just state a warning, aborts the operation execution only.
     * <strong>Notice that abortion here means interrupting the transformation.
     * It does not mean rolling back the changes that have might already been done
     * by this transformation operation by the time it failed</strong>
     *
     * @param abort if set to true, abort the whole transformation if validation or execution fails.
     *              If not, just state a warning, aborts the operation execution only
     * @param abortionMessage a message to be logged if a fail happens and transformation
     *                        has to be aborted
     * @return this transformation utility instance
     */
    public final T abortOnFailure(boolean abort, String abortionMessage) {
        abortOnFailure = abort;
        this.abortionMessage = abortionMessage;
        return (T) this;
    }

    /**
     * Returns a message to be logged if a fail happens and transformation has to be aborted
     *
     * @return a message to be logged if a fail happens and transformation has to be aborted
     */
    public String getAbortionMessage() {
        return abortionMessage;
    }

    /**
     * Returns whether this operation aborts the transformation or not in
     * case of an operation failure. Notice that this method does NOT
     * change the state this object in any ways, it is just a getter.
     *
     * @return true only if this operation aborts the transformation or not in
     * case of an operation failure
     */
    public final boolean abortOnFailure() {
        return abortOnFailure;
    }

    /**
     * This flag indicates whether the value produced by the transformation utility execution,
     * and also its result object as a whole, should both be saved in the transformation
     * context object.
     * <br>
     * In most cases it should do so, because that is the main purpose of
     * every transformation utility, to produce and share useful data with other
     * transformation utilities and operations.
     * <br>
     * However, there are rare cases,
     * for example {@link com.paypal.butterfly.extensions.api.utilities.Log},
     * where no value will be produced and nothing should be saved to the
     * transformation context attribute
     *
     * @return true only if the value produced by the transformation utility execution,
     * and also its result object as a whole, should both be saved in the transformation
     * context object
     */
    public boolean isSaveResult() {
        return saveResult;
    }

    /**
     * Sets whether or not the value produced by the transformation utility execution,
     * and also its result object as a whole, should both be saved in the transformation
     * context object. See also {@link #isSaveResult()}.
     *
     * @param saveResult if the value produced by the transformation utility execution,
     * and also its result object as a whole, should both be saved in the transformation
     * context object
     * @return this transformation utility instance
     */
    protected T setSaveResult(boolean saveResult) {
        this.saveResult = saveResult;
        return (T) this;
    }

    /**
     * Returns true only if this utility has already been performed
     *
     * @return true only if this utility has already been performed
     */
    public final boolean hasBeenPerformed() {
        return hasBeenPerformed.get();
    }

    /**
     * Add all transformation utilities this utility depends on.
     * Notice that this is not cumulative, meaning if this method has been called previously,
     * that dependencies set will be entirely replaced by this new one.
     * <br>
     * This notion of "dependency" among TUs help resilience in two ways:
     * <ol>
     *     <li>If TU B depends on TU A, and if TU A "fails"
     *     but doesn't abort transformation, then TU B would be skipped</li>
     *     <li>If TU B depends on TU A, then that means TU A is necessary supposed to be executed first,
     *     if not, TU B will be skipped</li>
     * </ol>
     * The term "fails" in this context means the perform result is of one of these types:
     * <ol>
     *     <li>{@link PerformResult.Type#ERROR}</li>
     *     <li>{@link PerformResult.Type#SKIPPED_CONDITION}</li>
     *     <li>{@link PerformResult.Type#SKIPPED_DEPENDENCY}</li>
     * </ol>
     * A dependency failure is also possible if perform result type is {@link PerformResult.Type#EXECUTION_RESULT},
     * and the execution result type is one of the following:
     * <ol>
     *     <li>{@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#NULL} (for TUs only)</li>
     *     <li>{@link com.paypal.butterfly.extensions.api.TUExecutionResult.Type#ERROR} (for TUs only)</li>
     *     <li>{@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#ERROR} (for TOs only)</li>
     * </ol>
     * <br>
     * See also:
     * <ul>
         * <li>{@link #checkDependencies(TransformationContext)}</li>
         * <li>{@link Result#dependencyFailureCheck()}</li>
         * <li>{@link TUExecutionResult#dependencyFailureCheck()}</li>
         * <li>{@link TOExecutionResult#dependencyFailureCheck()}</li>
         * <li>{@link PerformResult#dependencyFailureCheck()}</li>
     * </ul>
     *
     * @param dependencies the names of all transformation utilities this utility depends on
     * @return this transformation utility instance
     */
    public final T dependsOn(String... dependencies) {
        if (dependencies != null) {
            for (String dependency : dependencies) {
                if (StringUtils.isBlank(dependency)) throw new IllegalArgumentException("Dependencies cannot be null nor blank");
            }
        }
        this.dependencies = dependencies;
        return (T) this;
    }

    /**
     * Returns an unmodifiable list of names of utilities this utility instance depends on.
     * See also {@link #dependsOn(String...)}.
     *
     * @return an unmodifiable list of names of utilities this utility instance depends on
     */
    protected final List<String> getDependencies() {
        if (dependencies != null) {
            return Collections.unmodifiableList(Arrays.asList(dependencies));
        }
        return Collections.emptyList();
    }

    /**
     * Check if any of dependency of this TU failed. If that is true,
     * returns a result object stating so. If not, returns null. If this TU
     * has no dependencies it also returns null. See {@link #dependsOn(String...)}
     * to find out the dependency failure criteria
     *
     * @return a result object if any of dependency of this utility failed,
     * or null, if that is not the case, or if this utility does not have dependencies
     * @param transformationContext the transformation context object, in this case used
     *                              to check all past executed utilities
     */
    protected Result checkDependencies(TransformationContext transformationContext) {
        List<String> dependencies = getDependencies();
        PerformResult dependencyResult;
        String failedDependency = null;
        String failedDependencyResult = null;
        for(String dependency : dependencies) {
            dependencyResult = transformationContext.getResult(dependency);
            if (dependencyResult == null) {
                // This dependency has not even been executed, which
                // is considered as failure for dependency check
                failedDependency = dependency;
                break;
            }

            PerformResult.Type type = dependencyResult.getType();

            if (dependencyResult.dependencyFailureCheck()) {
                failedDependency = dependency;
                failedDependencyResult = type.name();
                break;
            }
        }
        if (failedDependency != null) {
            String details;
            if (failedDependencyResult != null) {
                details = String.format("%s was skipped because its dependency %s resulted in %s", getName(), failedDependency, failedDependencyResult);
            } else {
                details = String.format("%s was skipped because its dependency %s has not been executed yet", getName(), failedDependency);
            }
            return PerformResult.skippedDependency(this, details);
        }

        return null;
    }

    /**
     * When set, this TU will only execute if this transformation context
     * attribute is existent and true. In other words, it will execute if
     * not null and, if of Boolean type, true
     *
     * @param conditionAttributeName the name of the transformation context attribute which
     *                               holds a boolean value used to evaluate if this
     *                               utility should be executed or not
     * @return this transformation utility instance
     */
    public final T executeIf(String conditionAttributeName) {
        this.ifConditionAttributeName = conditionAttributeName;
        return (T) this;
    }

    /**
     * When set, this TU will only execute if this {@code utilityCondition} object,
     * executed right before this TU, result in true.
     * <br>
     * Differences between this approach and {@link #executeIf(String)}:
     * <ol>
     *     <li>Instead of relying on a TCA ({@link TransformationContext attribute}) with the condition result, this method is based on the direct execution of the {@link UtilityCondition} object</li>
     *     <li>The {@link UtilityCondition} object is always executed necessarily against the same file. Because of that, any value set on it via {@link #relative(String)} or {@link #absolute(String)} is ignored.</li>
     *     <li>The {@link UtilityCondition} object does not produce any TCA, neither its result value or result object. Instead, it hands its result directly to the TU, so that the condition can be evaluated just before the TU executes (or not, if it fails).</li>
     *     <li>The {@link UtilityCondition} object does not exist from a transformation template point of view. That means this method is totally different than adding a new {@link UtilityCondition} object by calling {@link TransformationTemplate#add(TransformationUtility)}.</li>
     *     <li>No TU can {@link #dependsOn(String...)} this {@link UtilityCondition} object.</li>
     * </ol>
     * <strong>The actual {@link UtilityCondition} object is not the one used, but a clone of it</strong>
     *
     * @param utilityCondition the condition to be executed and evaluated right before this TU
     * @return this transformation utility instance
     */
    public final T executeIf(UtilityCondition utilityCondition) {
        this.utilityCondition = utilityCondition;
        return (T) this;
    }

    /**
     * When set, this TU will execute, unless this transformation context
     * attribute is existent and true. In other words, it will execute, unless if
     * not null and, if of Boolean type, true
     *
     * @param conditionAttributeName the name of the transformation context attribute which
     *                               holds a boolean value used to evaluate if this
     *                               utility should be executed or not
     * @return this transformation utility instance
     */
    public final T executeUnless(String conditionAttributeName) {
        this.unlessConditionAttributeName = conditionAttributeName;
        return (T) this;
    }

    /**
     * Return the "if" condition attribute name associated with this transformation operation,
     * or null, if there is none
     *
     * @return the "if" condition attribute name associated with this transformation operation
     */
    public String getIfConditionAttributeName() {
        return ifConditionAttributeName;
    }

    /**
     * Return the "unless" condition attribute name associated with this transformation operation,
     * or null, if there is none
     *
     * @return the "unless" condition attribute name associated with this transformation operation
     */
    public String getUnlessConditionAttributeName() {
        return unlessConditionAttributeName;
    }

    /**
     * The implementation execution of this transformation utility.
     * The returned object is the result of the execution and is always
     * automatically saved in the transformation context as a new
     * attribute (whose key is the name of the transformation utility), unless
     * {@link #isSaveResult()} returns false.
     * <br>
     * <strong>Important: this method MUST NEVER return null, and it must catch its executions exceptions
     * and wrap them into a {@link ExecutionResult} error object</strong>.
     *
     * @param transformedAppFolder the folder where the transformed application code is
     * @param transformationContext the transformation context object
     *
     * @return an object with the result of this execution, to be better defined
     * by the concrete utility class, since its type is generic
     */
    protected abstract ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext);

    /**
     * Return true only if a file has been set. Every {@link TransformationUtility} has its file set automatically by
     * default to "" which means the root of the application. That is NOT the case though for {@link TransformationOperation}
     * object, which must set them explicitly via {@link #relative(String)} or {@link #absolute(String)}.
     *
     * @return true only if a file has been set
     */
    public final boolean isFileSet() {
        return !(getRelativePath() == null && getAbsoluteFileFromContextAttribute() == null);
    }

    /**
     * Return true only if a file has been set explicitly either via {@link #relative(String)} or {@link #absolute(String)}.
     * If set via {@link #relative(String)} it will only return true if set to anything other than "", which would mean the root of the application.
     *
     * @return true only if a file has been set explicitly either via {@link #relative(String)} or {@link #absolute(String)}
     */
    public final boolean wasFileExplicitlySet() {
        return !(StringUtils.isBlank(getRelativePath()) && getAbsoluteFileFromContextAttribute() == null);
    }

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public TransformationUtility<T> clone() throws CloneNotSupportedException {
        TransformationUtility<T> clone = (TransformationUtility<T>) super.clone();

        // Properties we do NOT want to be in the clone (they are being initialized)
        clone.hasBeenPerformed = new AtomicBoolean(false);

        // Properties we want to be in the clone (they are being copied from original object)
        clone.order = this.order;
        clone.parent = this.parent;
        clone.name = this.name;
        clone.relativePath = this.relativePath;
        clone.absoluteFile = this.absoluteFile;
        clone.absoluteFileFromContextAttribute = this.absoluteFileFromContextAttribute;
        clone.additionalRelativePath = this.additionalRelativePath;
        clone.contextAttributeName = this.contextAttributeName;
        clone.latePropertiesAttributes = new HashMap<String, String>();
        clone.latePropertiesSetters = new HashMap<String, Method>();
        clone.latePropertiesAttributes.putAll(this.latePropertiesAttributes);
        clone.latePropertiesSetters.putAll(this.latePropertiesSetters);
        clone.abortOnFailure = this.abortOnFailure;
        clone.saveResult = this.saveResult;
        clone.ifConditionAttributeName = this.ifConditionAttributeName;
        clone.unlessConditionAttributeName = this.unlessConditionAttributeName;
        clone.utilityCondition = this.utilityCondition;

        return clone;
    }

    /**
     * Creates and returns a brand new utility object using the original as a template,
     * and setting to the copy most of the attributes of the original one.
     * It will not copy though all attributes that define the identity of the original one, which are:
     * <ol>
     *  <li>parent</li>
     *  <li>name</li>
     *  <li>order</li>
     *  <li>file relative and absolute path</li>
     *  <li>context attribute name</li>
     * </ol>
     *
     * @return this transformation utility instance
     * @throws CloneNotSupportedException in case the concrete transformation utility
     *         does not support being cloned
     */
    public TransformationUtility<T> copy() throws CloneNotSupportedException {
        TransformationUtility<T> copy = (TransformationUtility<T>) super.clone();

        // Properties we do NOT want to be in the copy (they are being initialized)
        copy.order = -1;
        copy.parent = null;
        copy.name = null;
        copy.relativePath = "";
        copy.absoluteFile = null;
        copy.absoluteFileFromContextAttribute = null;
        copy.additionalRelativePath = null;
        copy.contextAttributeName = null;
        copy.hasBeenPerformed = new AtomicBoolean(false);

        // Properties we want to be in the copy (they are being copied from original object)
        copy.latePropertiesAttributes = new HashMap<String, String>();
        copy.latePropertiesSetters = new HashMap<String, Method>();
        copy.latePropertiesAttributes.putAll(this.latePropertiesAttributes);
        copy.latePropertiesSetters.putAll(this.latePropertiesSetters);
        copy.abortOnFailure = this.abortOnFailure;
        copy.saveResult = this.saveResult;
        copy.ifConditionAttributeName = this.ifConditionAttributeName;
        copy.unlessConditionAttributeName = this.unlessConditionAttributeName;
        copy.utilityCondition = this.utilityCondition;

        return copy;
    }

    /**
     * Check if value is a blank String, if it is, then a
     * {@link TransformationDefinitionException} is thrown.
     * <br>
     * This check is used for mandatory properties where value cannot be null
     * neither an empty string.
     *
     * @param name the name of the property
     * @param value the value to be verified
     * @throws TransformationDefinitionException if check fails
     */
    protected static void checkForBlankString(String name, String value) throws TransformationDefinitionException{
        if (StringUtils.isBlank(value)) {
            throw new TransformationDefinitionException(name + " cannot be blank");
        }
    }

    /**
     * Check if value is an empty String, if it is, then a
     * {@link TransformationDefinitionException} is thrown.
     * <br>
     * This check is used for optional properties where value can be null,
     * but not an empty string.
     *
     * @param name the name of the property
     * @param value the value to be verified
     * @throws TransformationDefinitionException if check fails
     */
    protected static void checkForEmptyString(String name, String value) throws TransformationDefinitionException{
        if (value != null && value.trim().length() == 0) {
            throw new TransformationDefinitionException(name + " cannot be empty");
        }
    }

    /**
     * Check if value is null, if it is, then a
     * {@link TransformationDefinitionException} is thrown.
     * <br>
     * This check is used for mandatory non-String properties,
     * where value cannot be null
     *
     * @param name the name of the property
     * @param value the value to be verified
     * @throws TransformationDefinitionException if check fails
     */
    protected static void checkForNull(String name, Object value) throws TransformationDefinitionException{
        if (value == null) {
            throw new TransformationDefinitionException(name + " cannot be null");
        }
    }

    /**
     * Compare this instance against the specified object, and return
     * true only if they are equal. Notice though that the fact that
     * the utility has been performed or not will NOT be used for this
     * comparison.
     *
     * @param obj the object to be compared against this instance
     * @return true only if they are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TransformationUtility)) return false;

        TransformationUtility tu = (TransformationUtility) obj;

        if (!Objects.equals(this.abortionMessage, tu.abortionMessage)) return false;
        if (this.abortOnFailure != tu.abortOnFailure) return false;
        if (!Objects.equals(this.absoluteFile, tu.absoluteFile)) return false;
        if (!Objects.equals(this.absoluteFileFromContextAttribute, tu.absoluteFileFromContextAttribute)) return false;
        if (!Objects.equals(this.additionalRelativePath, tu.additionalRelativePath)) return false;
        if (!Objects.equals(this.contextAttributeName, tu.contextAttributeName)) return false;
        if (!Arrays.equals(this.dependencies, tu.dependencies)) return false;
        if (!Objects.equals(this.ifConditionAttributeName, tu.ifConditionAttributeName)) return false;
        if (!Objects.equals(this.latePropertiesAttributes, tu.latePropertiesAttributes)) return false;
        if (!Objects.equals(this.name, tu.name)) return false;
        if (this.order != tu.order) return false;
        if (!Objects.equals(this.parent, tu.parent)) return false;
        if (!Objects.equals(this.relativePath, tu.relativePath)) return false;
        if (this.saveResult != tu.saveResult) return false;
        if (!Objects.equals(this.unlessConditionAttributeName, tu.unlessConditionAttributeName)) return false;
        if (!Objects.equals(this.utilityCondition, tu.utilityCondition)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hashCode(1,
                abortionMessage,
                abortOnFailure,
                absoluteFile,
                absoluteFileFromContextAttribute,
                additionalRelativePath,
                contextAttributeName,
                dependencies,
                ifConditionAttributeName,
                latePropertiesAttributes,
                name,
                order,
                parent,
                relativePath,
                saveResult,
                unlessConditionAttributeName,
                utilityCondition
        );
    }

    /**
     * Calculates and return a hash code starting from the
     * hash code generated from superclass
     *
     * @param superHashCode hash code generated from superclass
     * @param elements array of Objects to be used to generate hashcode.
     *               These elements should be the attributes used in
     *               the equals method
     * @return the generated hashcode
     */
    protected final int hashCode(int superHashCode, Object... elements) {
        if (elements == null) {
            return superHashCode;
        }
        int result = superHashCode;
        for (Object element : elements) {
            result = 31 * result + (element == null ? 0 : element.hashCode());
        }

        return result;
    }

}
