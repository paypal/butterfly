package com.paypal.butterfly.extensions.api.operations;

import com.paypal.butterfly.extensions.api.TransformationOperation;

/**
 * This interface should be implemented by {@link TransformationOperation}
 * subclasses that intend to modify a project by changing or modify elements,
 * standardizing the behavior and API in the absence of the element to be manipulated.
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
public interface ChangeOrRemoveElement<T extends TransformationOperation> {

    /**
     * Possible behaviors in case the element to be changed or removed is not present.
     * Each of these options is related to instances of {@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type},
     * although they are intentionally not supposed to match necessarily one-to-one.
     */
    enum IfNotPresent {

        /**
         * Fail if the element to be changed or removed is not present
         */
        Fail,

        /**
         * Warn if the element to be changed or removed is not present
         */
        NoOp,

        /**
         * Do nothing, not warn neither fail, if the element to be changed or removed is not present
         */
        Warn
    }

    /**
     * Fail ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#ERROR})
     * if the element to be changed or removed is not present
     *
     * @return the transformation operation instance
     */
    T failIfNotPresent();

    /**
     * Warn ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#WARNING})
     * if the element to be changed or removed is not present
     *
     * @return the transformation operation instance
     */
    T warnIfNotPresent();

    /**
     * Do nothing, not warn neither fail,  ({@link com.paypal.butterfly.extensions.api.TOExecutionResult.Type#NO_OP})
     * if the element to be changed or removed is not present
     *
     * @return the transformation operation instance
     */
    T noOpIfNotPresent();

}
