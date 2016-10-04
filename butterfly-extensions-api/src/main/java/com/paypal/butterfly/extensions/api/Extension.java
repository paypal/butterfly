package com.paypal.butterfly.extensions.api;

import java.util.*;

/**
 * A Butterfly third-party extension. It provides custom
 * transformation templates and validations
 *
 * @author facarvalho
 */
public abstract class Extension<E> {

    private List<Class<? extends TransformationTemplate>> templateClasses = new ArrayList<>();

    /**
     * Adds a new transformation template class to the set
     *
     * @param templateClass
     *
     * @return this extension
     */
    protected final E add(Class<? extends TransformationTemplate> templateClass) {
        templateClasses.add(templateClass);
        return (E) this;
    }

    /**
     * Returns the extension description
     *
     * @return the extension description
     */
    public abstract String getDescription();

    /**
     * Returns a read-only set containing all transformation template classes
     *
     * @return a read-only set containing all transformation template classes
     */
    public final List<Class<? extends TransformationTemplate>> getTemplateClasses() {
        return Collections.unmodifiableList(templateClasses);
    }

    @Override
    public final String toString() {
        return getClass().getName();
    }

}
