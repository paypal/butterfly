package com.paypal.butterfly.extensions.api.operations;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * This interface should be implemented by operations that expect to add elements,
 * standardizing in the TO what to do if the element to be added already exists.
 * </br>
 * Examples of elements to be added by TOs: files, folders, properties in properties files,
 * POM dependencies, POM managed dependencies, POM plugins, POM managed plugins, POM properties, etc,
 *
 * @author facarvalho
 */
public interface AddElement<TO extends TransformationOperation> {

    enum IfPresent {
        Fail, WarnNotAdd, WarnButAdd, NoOp, Overwrite
    }

    /**
     * Fail ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#ERROR})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    TO failIfPresent();

    /**
     * Warn and do not add ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#WARNING})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    TO warnNotAddIfPresent();

    /**
     * Warn, but add, ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#WARNING})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    TO warnButAddIfPresent();

    /**
     * Do nothing, not add, not warn neither fail, ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#NO_OP})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    TO noOpIfPresent();

    /**
     * Overwrite and not warn ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#SUCCESS})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    TO overwriteIfPresent();

}
