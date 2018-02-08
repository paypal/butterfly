package com.paypal.butterfly.extensions.api;

import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;

import java.io.File;

public interface AutomaticResolution {

    /**
     * Butterfly might be able to automatically identify, based on the application
     * content, the most applicable transformation template to transform it.
     * If no template applies to the application content, a {@link TemplateResolutionException}
     * is thrown explaining the reason why no template could be chosen.
     *
     * @param applicationFolder the folder where the code of the application to be transformed is
     * @return the chosen transformation template
     * @throws TemplateResolutionException if no template applies
     */
    Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException;

}
