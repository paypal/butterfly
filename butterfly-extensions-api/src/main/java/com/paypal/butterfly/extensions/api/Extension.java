package com.paypal.butterfly.extensions.api;

import java.util.Set;

/**
 * A Butterfly third-party extension. It provides custom
 * transformation templates and validations
 *
 * @author facarvalho
 */
public abstract class Extension {

    /**
     * Returns the extension description
     *
     * @return the extension description
     */
    public abstract String getDescription();

    /**
     * Returns a set containing all transformation template classes
     *
     * @return a set containing all transformation template classes
     */
    public abstract Set<Class<? extends TransformationTemplate>> getTemplateClasses();

    @Override
    public String toString() {
        return getClass().getName();
    }

}
