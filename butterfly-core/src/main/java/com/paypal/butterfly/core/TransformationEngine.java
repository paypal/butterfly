package com.paypal.butterfly.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The transformation engine in charge of
 * applying transformations
 *
 * @author facarvalho
 */
@Component
public class TransformationEngine {

    Logger logger = LoggerFactory.getLogger(TransformationEngine.class);

    @Autowired
    private ExtensionRegistry extensionRegistry;

    public void perform(Transformation transformation) {
        // TODO

        logger.debug("transformation requested: " + transformation);
    }

}
