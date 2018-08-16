package com.paypal.butterfly.cli;


import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;

/**
 * Type enumeration for {@link TransformationTemplate}
 * @author facarvalho
 */
enum TemplateType {

    TransformationTemplate("TT"), UpgradeStep("US");

    private final String initials;

    TemplateType(String initials) {
        this.initials = initials;
    }

    /**
     * Return a String representing a short version identifier for this template type,
     * useful for displaying a list of transformation templates
     *
     * @return a String representing a short version identifier for this template type
     */
    String getInitials() {
        return initials;
    }

    static TemplateType getFromClass(Class<? extends TransformationTemplate> template) {
        if(UpgradeStep.class.isAssignableFrom(template)) return UpgradeStep;
        if(TransformationTemplate.class.isAssignableFrom(template)) return TransformationTemplate;
        throw new IllegalArgumentException("Class " + template.getName() + " is not recognized as an extension type");
    }

}
