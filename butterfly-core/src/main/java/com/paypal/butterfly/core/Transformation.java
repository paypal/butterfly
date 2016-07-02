package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Represents an specific transformation to be applied
 * against a specific application
 *
 * @author facarvalho
 */
public class Transformation {

    private static final String TO_STRING_SYNTAX = "Transformation { \"application\" : {%s}, \"templateClass\" : %s }";

    private Application application;

    private TransformationTemplate template;

    public Transformation(Application application, TransformationTemplate template) {
        this.application = application;
        this.template = template;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_SYNTAX, application, template);
    }

}
