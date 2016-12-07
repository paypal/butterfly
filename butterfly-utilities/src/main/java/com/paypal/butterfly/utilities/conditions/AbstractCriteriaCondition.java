package com.paypal.butterfly.utilities.conditions;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.extensions.api.TransformationContext;
import com.paypal.butterfly.extensions.api.UtilityCondition;
import com.paypal.butterfly.extensions.api.exception.TransformationUtilityException;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Condition to check if a particular criteria is met in one
 * or more properties files. Multiple files can be  specified (via {@link #setFiles(String...)}).
 * While one file can be specified by regular {@link #relative(String)} and
 * {@link #absolute(String)} methods.
 * </br>
 * When evaluating multiple files, the criteria is configurable, and
 * can be al of them have the property, or at least one of them has it.
 * </br>
 * These three behaviors are set by the following methods:
 * <ol>
 *     <li>{@link #singleFile()}</li>
 *     <li>{@link #multipleFilesAtLeastOne()}</li>
 *     <li>{@link #multipleFilesAll()}</li>
 * </ol>
 * <strong>Notes:</strong>
 * <ol>
 *     <li>{@link #singleFile()} is the default behavior if none is specified</li>
 *     <li>When a multiple files method is set, the file specified by {@link #relative(String)}
 *     or {@link #absolute(String)} is ignored</li>
 * </ol>
 *
 * @author facarvalho
 */
public abstract class AbstractCriteriaCondition<C> extends UtilityCondition<C> {

    private enum Mode {
        SINGLE, MULTI_ONE, MULTI_ALL
    }

    private Mode mode = Mode.SINGLE;

    // Array of transformation context attributes that hold list of Files
    // which the condition should perform against.
    // If more than one attribute is specified, all list of files will be
    // combined into a single one.
    private String[] filesAttributes;

    /**
     * Sets one or more transformation context attributes that hold list of Files
     * which the condition should perform against.
     * If more than one attribute is specified, all list of files will be
     * combined into a single one.</br>
     * When calling this method, automatically the evaluation criteria is set to
     * "at least one" match, unless {@link #multipleFilesAll()} had been called previously.
     * If "all" is preferred instead, then call {@link #multipleFilesAll()}.
     *
     * @param filesAttributes one or more transformation context attributes that hold list
     *                   of Files which the condition should perform
     *                   against
     * @return this transformation utility object
     */
    public C setFiles(String... filesAttributes) {
        this.filesAttributes = filesAttributes;
        if (mode.equals(Mode.SINGLE)) {
            mode = Mode.MULTI_ONE;
        }
        return (C) this;
    }

    /**
     * Only the file specified by {@link #relative(String)} or
     * {@link #absolute(String)} is evaluated
     */
    public void singleFile() {
        mode = Mode.SINGLE;
    }

    /**
     * All files specified by {@link #setFiles(String...)} are
     * evaluated, and true is returned if at least one of
     * them has the dependency.
     * </br>
     * The file specified by {@link #relative(String)} or
     * {@link #absolute(String)} is ignored
     */
    public C multipleFilesAtLeastOne() {
        mode = Mode.MULTI_ONE;
        return (C) this;
    }

    /**
     * All files specified by {@link #setFiles(String...)} are
     * evaluated, and true is returned only if all of them
     * have the dependency.
     * </br>
     * The file specified by {@link #relative(String)} or
     * {@link #absolute(String)} is ignored
     */
    public C multipleFilesAll() {
        mode = Mode.MULTI_ALL;
        return (C) this;
    }

    public String[] getFilesAttributes() {
        return Arrays.copyOf(filesAttributes, filesAttributes.length);
    }

    @Override
    protected TUExecutionResult execution(File transformedAppFolder, TransformationContext transformationContext) {
        TUExecutionResult result = null;

        switch (mode) {
            case MULTI_ONE:
                // Same method call as MULTI_ALL
            case MULTI_ALL:
                Collection<File> files;
                Set<File> allFiles = new HashSet<>();
                for(String attribute: filesAttributes) {
                    files = (Collection<File>) transformationContext.get(attribute);
                    if (files != null) {
                        allFiles.addAll(files);
                    }
                }

                result = multipleFileExecution(transformedAppFolder, transformationContext, allFiles, mode.equals(Mode.MULTI_ALL));
                break;
            case SINGLE:
                // SINGLE is the default mode
            default:
                result = singleFileExecution(transformedAppFolder, transformationContext);
        }

        return result;
    }

    private TUExecutionResult singleFileExecution(File transformedAppFolder, TransformationContext transformationContext) {
        File file = getAbsoluteFile(transformedAppFolder, transformationContext);

        try {
            boolean exists = eval(transformedAppFolder, transformationContext, file);
            return TUExecutionResult.value(this, exists);
        } catch (TransformationUtilityException e) {
            return TUExecutionResult.error(this, e);
        }
    }

    /*
     * Perform this condition against a set of files
     *
     * @param transformedAppFolder the folder where the transformed application code is
     * @param transformationContext the transformation context object
     * @param files set of files to be evaluated
     * @param multiAll if true, all files must match the criteria, if false, at least one is enough
     *
     * @return the TU execution result object
     */
    private TUExecutionResult multipleFileExecution(File transformedAppFolder, TransformationContext transformationContext, Set<File> files, boolean multiAll) {
        if (files.size() == 0) {
            TransformationUtilityException e = new TransformationUtilityException("No pom files have been specified");
            return TUExecutionResult.error(this, e);
        }

        boolean result = false;
        try {
            for (File file : files) {
                result = eval(transformedAppFolder, transformationContext, file);
                if (!result && multiAll || result && !multiAll) {
                    break;
                }
            }
        } catch (TransformationUtilityException e) {
            return TUExecutionResult.error(this, e);
        }

        return TUExecutionResult.value(this, result);
    }

    protected abstract boolean eval(File transformedAppFolder, TransformationContext transformationContext, File file);

}
