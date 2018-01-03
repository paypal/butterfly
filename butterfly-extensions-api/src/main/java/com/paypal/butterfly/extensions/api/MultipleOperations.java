package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TransformationDefinitionException;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Transformation utility to perform multiple transformation operations. Multiple transformation operations
 * are defined based on an operation template and two other factors, that could be applied
 * exclusively or together. They are:
 * <ol>
 *     <li>Multiple files: multiple operations are defined based on multiple files specified as a
 *     list, held as one or more transformation context attribute, and set via {@link #setFiles(String...)}</li>
 *     <li>Multiple configurations: multiple operations are defined based on different configurations,
 *     set via {@link #setProperties(String, String)}</li>
 * </ol>
 * <br>
 * In other words, there are two possible ways to define multiple operations: multiple
 * files, or multiple configurations (different property values). It is also possible
 * to combine both, resulting in multiple operations to be executed against multiple files and
 * with multiple configurations.
 * <br>
 * <strong>Important:</strong> when running against multiple files, any path set to this operation,
 * either relative or absolute, will be ignored, and set later at transformation time based on
 * the dynamically set multiple files. When running with multiple configurations, the properties set
 * during transformation time will override any value that could have been set during definition time
 *
 * @author facarvalho
 */
// TODO Analyze if CopyFiles and DeleteFiles could be removed due to this utility
public class MultipleOperations extends TransformationUtility<MultipleOperations> implements TransformationUtilityParent {

    private static final Logger logger = LoggerFactory.getLogger(MultipleOperations.class);

    private static final String DESCRIPTION = "Perform operation %s against multiple files";

    // Array of transformation context attributes that hold list of Files
    // which the transformation operation should perform against.
    // If more than one attribute is specified, all list of files will be
    // combined into a single one.
    private String[] filesAttributes;

    // The operation Java bean property name to be set during transformation time,
    // whose value will differentiate each operation
    private String propertyName;

    // The name of the transformation context attribute that holds a collection of
    // values to be each set individually (as the property value) to each operation of
    // a set of multiple operations. These values are set right before execution. If
    // the transformation context attribute value is not a collection, then it will be
    // used as a single value to be set to one single operation, instead of multiple
    private String propertyAttribute;

    // This is the setter method in the template operation to be used to set the
    // property in case of multiple configurations
    private Method propertySetter;

    // A template of transformation operation to be performed against all
    // specified files
    private TransformationOperation templateOperation;

    // Actual operations to performed against all specified files
    private List<TransformationUtility> operations;

    /**
     * Utility to perform multiple transformation operations. Multiple transformation operations
     * are defined based on an operation template and two other factors, that could be applied
     * exclusively or together. They are:
     * <ol>
     *     <li>Multiple files: multiple operations are defined based on multiple files specified as a
     *     list, held as one or more transformation context attribute, and set via {@link #setFiles(String...)}</li>
     *     <li>Multiple configurations: multiple operations are defined based on different configurations,
     *     set via {@link #setProperties(String, String)}</li>
     * </ol>
     * <br>
     * In other words, there are two possible ways to define multiple operations: multiple
     * files, or multiple configurations (different property values). It is also possible
     * to combine both, resulting in multiple operations to be executed against multiple files and
     * with multiple configurations.
     * <br>
     * <strong>Important:</strong> when running against multiple files, any path set to this operation,
     * either relative or absolute, will be ignored, and set later at transformation time based on
     * the dynamically set multiple files. When running with multiple configurations, the properties set
     * during transformation time will override any value that could have been set during definition time
     */
    public MultipleOperations() {
    }

    /**
     * Utility to perform multiple transformation operations. Multiple transformation operations
     * are defined based on an operation template and two other factors, that could be applied
     * exclusively or together. They are:
     * <ol>
     *     <li>Multiple files: multiple operations are defined based on multiple files specified as a
     *     list, held as one or more transformation context attribute, and set via {@link #setFiles(String...)}</li>
     *     <li>Multiple configurations: multiple operations are defined based on different configurations,
     *     set via {@link #setProperties(String, String)}</li>
     * </ol>
     * <br>
     * In other words, there are two possible ways to define multiple operations: multiple
     * files, or multiple configurations (different property values). It is also possible
     * to combine both, resulting in multiple operations to be executed against multiple files and
     * with multiple configurations.
     * <br>
     * <strong>Important:</strong> when running against multiple files, any path set to this operation,
     * either relative or absolute, will be ignored, and set later at transformation time based on
     * the dynamically set multiple files. When running with multiple configurations, the properties set
     * during transformation time will override any value that could have been set during definition time
     *
     * @param templateOperation a template of transformation operation to be performed
     *                          against all specified files
     */
    public MultipleOperations(TransformationOperation templateOperation) {
        setOperationTemplate(templateOperation);
    }

    /**
     * Sets one or more transformation context attributes that hold list of Files
     * which the transformation operations should perform against.
     * If more than one attribute is specified, all list of files will be
     * combined into a single one
     *
     * @param filesAttributes one or more transformation context attributes that hold list
     *                   of Files which the transformation operation should perform
     *                   against
     * @return this transformation utility object
     */
    public MultipleOperations setFiles(String... filesAttributes) {
        this.filesAttributes = filesAttributes;
        return this;
    }

    /**
     * This setter is similar to {@link TransformationUtility#set(String, String)}, however it is more powerful, because
     * it allows setting, during transformation time, different properties values for each operation of a
     * {@link MultipleOperations}.
     * <br>
     * That being said, calling this method will only make a difference if this operation is executed as the
     * template operation for a multiple operations utility. That usually can be done by adding it to a
     * transformation template via {@link TransformationTemplate#addMultiple(TransformationOperation, String...)}
     *
     * @param propertyName the operation Java bean property name to be set during transformation time
     * @param propertyAttribute the name of the transformation context attribute that holds a {@link Set} of
     *                          values to be each set individually (as the property value) to each operation of
     *                          a set of multiple operations. These values are set right before execution. If
     *                          the transformation context attribute value is not a {@link Set}, then a
     *                          {@link com.paypal.butterfly.extensions.api.exception.TransformationUtilityException}
     *                          will be thrown right before execution
     * @return this instance
     */
    public final MultipleOperations setProperties(String propertyName, String propertyAttribute) {
        checkForBlankString("propertyName", propertyName);
        checkForBlankString("propertyAttribute", propertyAttribute);
        this.propertyName = propertyName;
        this.propertyAttribute = propertyAttribute;
        return this;
    }

    /**
     * Sets the template of transformation operation to be performed against all specified files.
     * <br>
     * <strong>Important:</strong> any path set to this operation, either relative
     * or absolute, will be ignored, and set later at transformation time based on
     * the dynamically set multiple files
     *
     * @param templateOperation the template of transformation operation to be performed against
     *                  all specified files
     * @return this transformation utility object
     */
    public MultipleOperations setOperationTemplate(TransformationOperation templateOperation) {
        templateOperation.relative(null);
        templateOperation.absolute(null);
        this.templateOperation = templateOperation;
        return this;
    }

    @Override
    public MultipleOperations setName(String name) {
        templateOperation.setName(String.format("%s-%s-TEMPLATE_OPERATION", name, templateOperation.getClass().getSimpleName()));
        return super.setName(name);
    }

    /**
     * Return an array containing the name of transformation context attributes
     * that hold the list of files the operations should be performed against
     *
     * @return an array containing the name of transformation context attributes
     * that hold the list of files the operations should be performed against
     */
    public String[] getFilesAttributes() {
        return Arrays.copyOf(filesAttributes, filesAttributes.length);
    }

    public TransformationOperation getTemplateOperation() {
        return templateOperation;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, templateOperation.getClass().getSimpleName());
    }

    public void setPropertySetter() {
        String methodName = String.format("set%s%s", propertyName.substring(0, 1).toUpperCase(), propertyName.substring(1));

        for(Method method : templateOperation.getClass().getMethods()) {
            if(method.getName().equals(methodName)) {
                propertySetter = method;
                return;
            }
        }

        String exceptionMessage = String.format("%s is not a valid property", propertyName);
        throw new TransformationDefinitionException(exceptionMessage);
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        Collection<File> files;
        Set<File> allFiles = new HashSet<>();

        for(String attribute: filesAttributes) {
            files = (Collection<File>) transformationContext.get(attribute);
            if (files != null) {
                allFiles.addAll(files);
            }
        }

        boolean multipleFiles = true;
        if (allFiles.size() == 0) {
            if (wasFileExplicitlySet()) {
                // This means this multiple operation is not supposed to be executed
                // based on multiple files, but based on multiple configuration
                // Because of that, the single file the multiple operations should
                // run against is defined as usual
                allFiles.add(getAbsoluteFile(transformedAppFolder, transformationContext));
                multipleFiles = false;
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Multiple operation %s has NO file to perform against, it will result in ZERO transformation operations", getName());
                }
                operations = new ArrayList<TransformationUtility>();
                String message = String.format("Multiple operation %s resulted in 0 operations based on %s", getName(), templateOperation.getClass().getSimpleName());
                return TUExecutionResult.value(this, getChildren()).setDetails(message);
            }
        }

        boolean multipleConfigurations = false;
        Set propertyValues = null;

        if (propertyName != null) {
            Object propertyValuesObj = transformationContext.get(propertyAttribute);
            if (!(propertyValuesObj instanceof Set)) {
                String exceptionMessage = String.format("Transformation context attribute %s does not contain a java.util.Set object", propertyAttribute);
                TransformationUtilityException tue = new TransformationUtilityException(exceptionMessage);
                return TUExecutionResult.error(this, tue);
            }
            propertyValues = (Set) propertyValuesObj;
            if (propertyValues.size() == 0) {
                logger.warn("Transformation context attribute %s contains an empty java.util.Set object, so it will be ignored", propertyAttribute);
            } else {
                multipleConfigurations = true;
                setPropertySetter();
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Multiple files in {} is set to {}", getName(), multipleFiles);
            logger.debug("Multiple configurations in {} is set to {}", getName(), multipleConfigurations);
        }

        TransformationOperation operation;
        operations = new ArrayList<TransformationUtility>();
        int order = 1;
        try {
            for(File file : allFiles) {
                if (!multipleConfigurations) {
                    operation = createClone(order, transformedAppFolder, file);
                    operations.add(operation);
                    order++;
                } else {
                    Object[] propertyValuesArray = propertyValues.toArray();
                    for (Object propertyValue : propertyValuesArray) {
                        operation = createClone(order, transformedAppFolder, file);
                        propertySetter.invoke(operation, propertyValue);
                        operations.add(operation);
                        order++;
                    }
                }
            }
        } catch (CloneNotSupportedException e) {
            TransformationUtilityException tue = new TransformationUtilityException("The template transformation operation is not cloneable", e);
            return TUExecutionResult.error(this, tue);
        } catch (InvocationTargetException | IllegalAccessException e) {
            String exceptionMessage = String.format("It was not possible to set property %s, in object of type %s, during multiple operation multiple configuration setting pre-execution", propertyName, templateOperation.getClass().getName());
            TransformationUtilityException tue = new TransformationUtilityException(exceptionMessage, e);
            return TUExecutionResult.error(this, tue);
        }

        String message = null;
        if(logger.isDebugEnabled()) {
            message = String.format("Multiple operation %s resulted in %d operations based on %s", getName(), operations.size(), templateOperation.getClass().getSimpleName());
        }
        return TUExecutionResult.value(this, getChildren()).setDetails(message);
    }

    private TransformationOperation createClone(int order, File transformedAppFolder, File file) throws CloneNotSupportedException {
        TransformationOperation operation = (TransformationOperation) templateOperation.copy();
        operation.setParent(this, order);
        operation.relative(TransformationUtility.getRelativePath(transformedAppFolder, file));

        return operation;
    }

    @Override
    public List<TransformationUtility> getChildren() {
        if (operations == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(operations);
    }

}
