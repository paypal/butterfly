package com.paypal.butterfly.core;

import com.paypal.butterfly.api.TransformationResult;

import java.util.List;

/**
 * Interface to abstract manual instruction writer implementation.
 * This interface is not part of Butterfly API though, it is just meant
 * to provide some decoupling for this particular internal feature.
 * <br>
 * There must be one ManualInstructionsWriter only, a singleton,
 * to be injected to the {@link TransformationEngine}.
 * <br>
 * The implementation details and file format are up to the concrete class.
 * <br>
 * Main file and instructions directory path and name are pre-defined and created (both empty).
 *
 * @author facarvalho
 */
public interface ManualInstructionsWriter {

    /**
     * Write post-transformation manual instruction files.
     * <br>
     * The implementation details and file format are up to the concrete class.
     * <br>
     * However, the main file (summarizing and linking individual manual instruction documents) and
     * instructions directory (where all manual instruction documents are) are defined as part of
     * this contract, must be obtained via {@link TransformationResult#getManualInstructionsDir()}
     * and {@link TransformationResult#getManualInstructionsFile()}, and are already created, as an
     * empty dir and an empty file, respectively.
     *
     * @param transformationResult the object containing information about the transformation result
     * @param transformationContexts a list of transformation context objects generated during this transformation,
     *                               if this was a regular transformation, it will contain only one element, if it was
     *                               an upgrade, there will be one item per upgrade step, ordered according to upgrade
     *                               steps execution
     * @throws InternalTransformationException if any error happens when writing files
     */
    void writeManualInstructions(TransformationResult transformationResult, List<TransformationContextImpl> transformationContexts) throws InternalTransformationException;

}