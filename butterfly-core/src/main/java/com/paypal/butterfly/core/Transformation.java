package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Represents an specific transformation to be applied
 * against a specific application
 *
 * @author facarvalho
 */
public class Transformation {

    private static final String TO_STRING_SYNTAX = "Transformation { \"application\" : {%s}, \"templateId\" : %s }";

    private Application application;

    private String templateId;

    public Transformation(Application application, String templateId) {
        this.application = application;
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_SYNTAX, application, templateId);
    }

}
