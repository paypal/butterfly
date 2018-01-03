package com.paypal.butterfly.extensions.api.operations;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * This interface should be implemented by {@link TransformationOperation}
 * subclasses that intend to modify a project by adding elements to it,
 * standardizing the behavior and API if the element to be added already exists.
 * <br>
 * <br>
 * Examples:
 * <ul>
 *     <li>files</li>
 *     <li>folders</li>
 *     <li>properties, in properties files</li>
 *     <li>POM dependencies</li>
 *     <li>POM managed dependencies</li>
 *     <li>POM plugins</li>
 *     <li>POM managed plugins</li>
 *     <li>POM properties</li>
 * </ul>
 *
 * @author facarvalho
 */
public interface AddElement<T extends TransformationOperation> {

    /**
     * Possible behaviors in case the element to be added already exists.
     * Each of these options is related to instances of {@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type},
     * although they are intentionally not supposed to match necessarily one-to-one.
     */
    enum IfPresent {

        /**
         * Fail if the element to be added is already present
         */
        Fail,

        /**
         * Warn and do not add if the element to be added is already present
         */
        WarnNotAdd,

        /**
         * Warn, but add, if the element to be added is already present
         */
        WarnButAdd,

        /**
         * Do nothing, not add, not warn neither fail, if the element to be added is already present
         */
        NoOp,

        /**
         * Overwrite and not warn if the element to be added is already present
         */
        Overwrite
    }

    /**
     * Fail ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#ERROR})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    T failIfPresent();

    /**
     * Warn and do not add ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#WARNING})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    T warnNotAddIfPresent();

    /**
     * Warn, but add, ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#WARNING})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    T warnButAddIfPresent();

    /**
     * Do nothing, not add, not warn neither fail, ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#NO_OP})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    T noOpIfPresent();

    /**
     * Overwrite and not warn ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#SUCCESS})
     * if the element to be added is already present
     *
     * @return the transformation operation instance
     */
    T overwriteIfPresent();

}
