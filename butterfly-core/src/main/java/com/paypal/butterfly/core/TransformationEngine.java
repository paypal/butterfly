package com.paypal.butterfly.core;

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

    @Autowired
    private ExtensionRegistry extensionRegistry;

    public void perform(Transformation transformation) {
        // TODO
    }

}
