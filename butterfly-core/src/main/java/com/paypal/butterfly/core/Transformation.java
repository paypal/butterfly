package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Represents an specific transformation to be applied
 * against a specific application
 *
 * @author facarvalho
 */
public class Transformation {

    // TODO Replace this by an actual JSON object mapper
    private static final String TO_STRING_SYNTAX = "\n\nTransformation {\n\t\"application\" : %s,\n\t\"template\" : %s,\n\t\"templateClass\" : %s\n}\n";

    private Application application;

    private TransformationTemplate template;

    public Transformation(Application application, TransformationTemplate template) {
        this.application = application;
        this.template = template;
    }

    public Application getApplication() {
        return application;
    }

    public TransformationTemplate getTemplate() {
        return template;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_SYNTAX, application, template, template.getClass().getName());
    }

}
