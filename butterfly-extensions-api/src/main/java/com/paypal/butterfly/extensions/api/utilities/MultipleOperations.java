package com.paypal.butterfly.extensions.api.utilities;

import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.TransformationOperation;
import com.paypal.butterfly.extensions.api.TransformationUtility;
import com.paypal.butterfly.extensions.api.TransformationUtilityParent;

import java.io.File;
import java.util.*;

/**
 * Utility to perform transformation operations against multiple files
 * </br>
 * <strong>Important:</strong> any path set to this operation, either relative
 * or absolute, will be ignored, and set later at transformation time based on
 * the dynamically set multiple files
 *
 * @author facarvalho
 */
// TODO Analyze if CopyFiles and DeleteFiles could be removed due to this utility
public class MultipleOperations extends TransformationUtility<MultipleOperations, List<TransformationOperation>> implements TransformationUtilityParent {

    private static final String DESCRIPTION = "Perform operation %s against multiple files";

    // Array of transformation context attributes that hold list of Files
    // which the transformation operation should perform against.
    // If more than one attribute is specified, all list of files will be
    // combined into a single one.
    private String[] attributes;

    // A template of transformation operation to be performed against all
    // specified files
    private TransformationOperation templateOperation;

    // Actual operations to performed against all specified files
    private List<TransformationOperation> operations;

    /**
     * Utility to perform one transformation operation against a list of files
     */
    public MultipleOperations() {
    }

    /**
     * Utility to perform one transformation operation against a list of files
     * </br>
     * <strong>Important:</strong> any path set to this operation, either relative
     * or absolute, will be ignored, and set later at transformation time based on
     * the dynamically set multiple files
     *
     * @param templateOperation a template of transformation operation to be performed
     *                          against all specified files
     * @param attributes one or more transformation context attributes that hold list
     *                   of Files which the transformation operations should perform
     *                   against
     */
    public MultipleOperations(TransformationOperation templateOperation, String... attributes) {
        setTemplateOperation(templateOperation);
        setAttributes(attributes);
    }

    /**
     * Sets one or more transformation context attributes that hold list of Files
     * which the transformation operations should perform against.
     * If more than one attribute is specified, all list of files will be
     * combined into a single one
     *
     * @param attributes one or more transformation context attributes that hold list
     *                   of Files which the transformation operation should perform
     *                   against
     * @return this transformation utility object
     */
    public MultipleOperations setAttributes(String... attributes) {
        this.attributes = attributes;
        return this;
    }

    /**
     * Sets the template of transformation operation to be performed against all specified files.
     * </br>
     * <strong>Important:</strong> any path set to this operation, either relative
     * or absolute, will be ignored, and set later at transformation time based on
     * the dynamically set multiple files
     *
     * @param templateOperation the template of transformation operation to be performed against
     *                  all specified files
     * @return this transformation utility object
     */
    public MultipleOperations setTemplateOperation(TransformationOperation templateOperation) {
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

    public String[] getAttributes() {
        return Arrays.copyOf(attributes, attributes.length);
    }

    public TransformationOperation getTemplateOperation() {
        return templateOperation;
    }

    @Override
    public String getDescription() {
        return String.format(DESCRIPTION, templateOperation.getClass().getSimpleName());
    }

    @Override
    protected List<TransformationOperation> execution(File transformedAppFolder, TransformationContext transformationContext) throws Exception {
        List<File> files;
        Set<File> allFiles = new HashSet<File>();

        for(String attribute: attributes) {
            files = (List<File>) transformationContext.get(attribute);
            allFiles.addAll(files);
        }

        operations = new ArrayList<TransformationOperation>();
        TransformationOperation operation;
        int order = 1;
        for(File file : allFiles) {
            operation = (TransformationOperation) templateOperation.clone();
            operation.setParent(this, order);
            operation.relative(TransformationUtility.getRelativePath(transformedAppFolder, file));
            order++;
            operations.add(operation);
        }

        return operations;
    }

    public List<TransformationOperation> getOperations() {
        return Collections.unmodifiableList(operations);
    }

}
