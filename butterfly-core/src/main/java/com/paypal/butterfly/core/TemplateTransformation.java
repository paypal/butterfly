package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.facade.Configuration;

/**
 * Represents an specific transformation, made of one single
 * {@link TransformationTemplate}, to be applied against a specific application
 *
 * @author facarvalho
 */
@SuppressWarnings("PMD.DefaultPackage")
public class TemplateTransformation extends Transformation {

    private static final String TO_STRING_SYNTAX = "{ \"application\" : %s, \"template\" : %s, \"templateClass\" : %s }";

    // The transformation template to be applied against an application
    private TransformationTemplate template;

    public TemplateTransformation(Application application, TransformationTemplate template, Configuration configuration) {
        super(application, configuration);
        if (template == null) {
            throw new IllegalArgumentException("Upgrade path cannot be null");
        }
        this.template = template;
    }

    public TransformationTemplate getTemplate() {
        return template;
    }

    @Override
    String getExtensionName() {
        return getExtensionName(template.getExtensionClass());
    }

    @Override
    String getExtensionVersion() {
        return getExtensionVersion(template.getExtensionClass());
    }

    @Override
    String getTemplateName() {
        return template.getName();
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_SYNTAX, getApplication(), template, template.getClass().getName());
    }

}
