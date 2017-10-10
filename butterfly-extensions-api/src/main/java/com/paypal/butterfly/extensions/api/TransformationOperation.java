package com.paypal.butterfly.extensions.api;


/**
 * Special type of {@link TransformationUtility} that applies a modification to the project.
 * <br>
 * Transformation operations are also known by {@code TO}.
 * <br>
 * Differences between a regular transformation utility (TU) and transformation operations (TO):
 * <ul>
 *     <li>TU never modifies application. TO always does.</li>
 *     <li>TU usually returns a value, but not necessarily. TO never does.</li>
 *     <li>TU usually saves its result, but not necessarily. TO always does.</li>
 *     <li>TO allows multiple operations.</li>
 * </ul>
 *<br>
 * The default value for {@link #relative(String)} is {@code null}, which means
 * it must be set explicitly, unless an absolute path is set via {@link #absolute(String)}
 * or {@link #absolute(String, String)}
 * <br>
 * Every transformation operation subclass must override {@link #clone()} and every operation
 * specific property defined in the operation subclass must be copied from the original
 * object to the clone object. Properties inherited from this class and its super classes
 * MUST NOT be copied from original object to cloned object, since that is all already taken
 * care of properly by the framework. Notice that name, parent and path (absolute and relative)
 * are NECESSARILY NOT assigned to the clone object
 *
 * @author facarvalho
 */
public abstract class TransformationOperation<TO extends TransformationOperation> extends TransformationUtility<TO> {

    public TransformationOperation() {
        // Different than regular Transformation Utilities, the default value here is null, which means
        // it must be set explicitly by the developer, unless an absolute path is set
        relative(null);
    }

    @Override
    protected final TO setSaveResult(boolean saveResult) {
        throw new UnsupportedOperationException("Transformation operations must always save results");
    }

}
