package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;

/**
 * Represents an specific transformation to be applied
 * against a specific application
 *
 * @author facarvalho
 */
public class Transformation {

    private Application application;

    private String templateId;

    public Transformation(Application application, String templateId) {
        this.application = application;
        this.templateId = templateId;
    }
}
