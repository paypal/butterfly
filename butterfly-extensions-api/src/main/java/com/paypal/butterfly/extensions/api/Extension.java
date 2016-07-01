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
     * Returns the extension id
     *
     * @return the extension id
     */
    String getId();

    /**
     * Returns the extension description
     *
     * @return the extension description
     */
    String getDescription();

    /**
     * Returns a set containing all transformation template ids
     *
     * @return a set containing all transformation template ids
     */
    Set<String> getTemplateIds();

    /**
     * Returns a transformation template based on its id
     *
     * @param templateId the transformation template id
     * @return the transformation template
     */
    TransformationTemplate getTemplate(String templateId);

}
