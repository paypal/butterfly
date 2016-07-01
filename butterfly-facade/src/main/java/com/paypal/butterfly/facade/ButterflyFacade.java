package com.paypal.butterfly.facade;

import java.io.File;

/**
 * Butterfly façade
 *
 * @author facarvalho
 */
public interface ButterflyFacade {

    /**
     * Transform an application
     *
     * @param applicationFolder application folder
     * @param templateId transformation template id
     */
    void transform(File applicationFolder, String templateId);

}