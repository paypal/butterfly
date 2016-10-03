package com.paypal.butterfly.extensions.api;


import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

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
// TODO create another type to be parent of TO and TU, this way the result type will be better organized
// How to name it? transformation node?
// This type should be the one to be added to a template
public abstract class TransformationUtility<TU> implements Cloneable {

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

    // Abort the whole transformation if this operation fails
    private boolean abortOnFailure = false;

    // See comments in isSaveResult method
    private boolean saveResult = true;

    // See comments in dependsOn method
    private String[] dependencies = null;

    // Optional condition to let this operation be executed
    // This is the name of a transformation context attribute
    // whose value is a boolean
    private String conditionAttributeName = null;

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
                // FIXME a better exception is necessary here for cases when the absolute path transformation context attribute value is null
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
        Object value = null;
        for (final Iterator itr = latePropertiesAttributes.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<String,String> entry = (Map.Entry)itr.next();
            String propertyName = entry.getKey();
            attributeName = latePropertiesAttributes.get(propertyName);
            try {
                method = latePropertiesSetters.get(propertyName);
                value = transformationContext.get(attributeName);
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
    public synchronized PerformResult perform(File transformedAppFolder, TransformationContext transformationContext) throws TransformationUtilityException {

        // Checking for conditions
        if(conditionAttributeName != null) {
            Object conditionResult = transformationContext.get(conditionAttributeName);
            if (conditionResult == null || (conditionResult instanceof Boolean && !((Boolean) conditionResult).booleanValue())) {
                String details = String.format("Operation '%s' has been skipped due to failing condition: %s", getName(), conditionAttributeName);
                return PerformResult.skippedCondition(this, details);
            }
        }

        // Checking for dependencies
        PerformResult result = (PerformResult) checkDependencies(transformationContext);
        if (result != null) {
            return result;
        }

        // Applying properties during transformation time
        applyPropertiesFromContext(transformationContext);

        try {
            ExecutionResult executionResult = execution(transformedAppFolder, transformationContext);
            result = PerformResult.executionResult(this, executionResult);

            return result;
        } catch(Exception e) {
            throw new TransformationUtilityException(getName() + " has failed", e);
        }
    }

    /**
     * If set to true, abort the whole transformation if validation or execution fails.
     * If not, just state a warning, aborts the operation execution only.
     * <strong>Notice that abortion here means interrupting the transformation.
     * It does not mean rolling back the changes that have might already been done
     * by this transformation operation by the time it failed<strong/>
     *
     * @param abort
     * @return
     */
    public final TU abortOnFailure(boolean abort) {
        abortOnFailure = abort;
        return (TU) this;
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
     * </br>
     * In most cases it should do so, because that is the main purpose of
     * every transformation utility, to produce and share useful data with other
     * transformation utilities and operations.
     * </br>
     * However, there are rare cases,
     * for example {@link com.paypal.butterfly.extensions.api.utilities.Log},
     * where no value will be produced and nothing should be saved to the
     * transformation context attribute
     *
     * @return
     */
    public boolean isSaveResult() {
        return saveResult;
    }

    /**
     * @see {@link #isSaveResult()}
     * @param saveResult
     */
    protected TU setSaveResult(boolean saveResult) {
        this.saveResult = saveResult;
        return (TU) this;
    }

    /**
     * Add all transformation utilities this utility depends on.
     * Notice that this is not cumulative, meaning if this method has been called previously,
     * that dependencies set will be entirely replaced by this new one.
     * </br>
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
     * </br>
     *
     * @see {@link #checkDependencies(TransformationContext)}
     * @see {@link Result#dependencyFailureCheck()}
     * @see {@link TUExecutionResult#dependencyFailureCheck()}
     * @see {@link TOExecutionResult#dependencyFailureCheck()}
     * @see {@link PerformResult#dependencyFailureCheck()}
     *
     * @param dependencies
     */
    public final TU dependsOn(String... dependencies) {
        this.dependencies = dependencies;
        return (TU) this;
    }

    /**
     * Returns dependencies
     *
     * @see {@link #dependsOn(String...)}
     *
     * @return
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
     * @return
     * @param transformationContext
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
                details = String.format("'%s' has been skipped because its dependency %s resulted in %s", getName(), failedDependency, failedDependencyResult);
            } else {
                details = String.format("'%s' has been skipped because its dependency %s has not been executed yet", getName(), failedDependency);
            }
            return PerformResult.skippedDependency(this, details);
        }

        return null;
    }

    /**
     * When set, this TU will only execute if this transformation context
     * attribute is existent and not false. In other words, it will execute if
     * not null and, if of Boolean type, not false
     *
     * @param conditionAttributeName
     * @return
     */
    public final synchronized TU executeIf(String conditionAttributeName) {
        this.conditionAttributeName = conditionAttributeName;
        return (TU) this;
    }

    /**
     * Return the condition attribute name associated with this transformation operation,
     * or null, if there is none
     *
     * @return the condition attribute name associated with this transformation operation
     */
    public String getConditionAttributeName() {
        return conditionAttributeName;
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
    protected abstract ExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext);

    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public TransformationUtility<TU> clone() throws CloneNotSupportedException {
        TransformationUtility<TU> clone = (TransformationUtility<TU>) super.clone();

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
        clone.latePropertiesAttributes = new HashMap<String, String>();
        clone.latePropertiesSetters = new HashMap<String, Method>();
        clone.latePropertiesAttributes.putAll(this.latePropertiesAttributes);
        clone.latePropertiesSetters.putAll(this.latePropertiesSetters);
        clone.abortOnFailure = this.abortOnFailure;
        clone.saveResult = this.saveResult;
        clone.conditionAttributeName = this.conditionAttributeName;

        return clone;
    }

    /**
     * To check for Blank String if it is Blank String then it would throw TransformationDefinitionException.
     * This check is required for non-optional properties.
     *
     * @param name represents the purpose of the String is used for
     * @param value represents the String to be verified
     * @throws TransformationDefinitionException
     */
    protected static void checkForBlankString(String name, String value) throws TransformationDefinitionException{
        if(StringUtils.isBlank(value)){
            throw new TransformationDefinitionException(name + " cannot be blank");
        }
    }

    /**
     * To check for an Empty String if it is an Empty String then it would throw TransformationDefinitionException
     * This check is required for optional properties where value can be possibly blank, if value is passed
     * then it should not be an empty string.
     *
     * @param name represents the purpose of the String is used for
     * @param value represents the String to be verified
     * @throws TransformationDefinitionException
     */
    protected static void checkForEmptyString(String name, String value) throws TransformationDefinitionException{
        if(value != null && value.trim().length() == 0){
            throw new TransformationDefinitionException(name + " cannot be empty");
        }
    }

    /**
     * To check for object is null if it is null then it would throw TransformationDefinitionException
     * This check is required where value can't be null.
     *
     * @param name represents the purpose of the String is used for
     * @param value represents the object value to be verified
     * @throws TransformationDefinitionException
     */
    protected static void checkForNull(String name, Object value) throws TransformationDefinitionException{
        if(value == null){
            throw new TransformationDefinitionException(name + " cannot be null");
        }
    }

}
