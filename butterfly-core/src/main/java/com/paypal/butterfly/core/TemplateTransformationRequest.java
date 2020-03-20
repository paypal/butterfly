package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.api.Application;
import com.paypal.butterfly.api.Configuration;

/**
 * Represents an specific transformation, made of one single
 * {@link TransformationTemplate}, to be applied against a specific application
 *
 * @author facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
class TemplateTransformationRequest extends AbstractTransformationRequest {

    private transient static final String TO_STRING_SYNTAX = "{ \"application\" : %s, \"template\" : %s, \"templateClass\" : %s }";

    // The transformation template to be applied against an application
    private transient TransformationTemplate template;

    TemplateTransformationRequest(Application application, TransformationTemplate template, Configuration configuration) {
        super(application, configuration, template.isBlank());
        if (template == null) {
            throw new IllegalArgumentException("Upgrade path cannot be null");
        }
        this.template = template;

        extensionName = getExtensionName(template.getExtensionClass());
        extensionVersion = getExtensionVersion(template.getExtensionClass());
        templateName = template.getName();
        templateClassName = template.getClass().getName();
        upgradeStep = false;
    }

    TransformationTemplate getTemplate() {
        return template;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_SYNTAX, getApplication(), template, templateClassName);
    }

}
