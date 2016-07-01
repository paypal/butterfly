package com.paypal.butterfly.extensions.api;

import java.util.Set;

/**
 * A Butterfly third-party extension. It provides custom
 * transformation templates and validations
 *
 * @author facarvalho
 */
public interface Extension {

    /**
     * Returns the extensio nname
     *
     * @return the extension name
     */
    String getName();

    /**
     * Returns a set containing all transformation template names
     *
     * @return a set containing all transformation template names
     */
    Set<String> getTemplateNames();

    /**
     * Returns a transformation template based on its name
     *
     * @param templateName the transformation template name
     * @return the transformation template
     */
    TransformationTemplate getTemplate(String templateName);

}
