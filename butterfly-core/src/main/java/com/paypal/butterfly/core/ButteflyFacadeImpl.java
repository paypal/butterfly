package com.paypal.butterfly.core;

import com.paypal.butterfly.facade.ButterflyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * Buttefly Façade implementation
 *
 * @author facarvalho
 */
@Component
public class ButteflyFacadeImpl implements ButterflyFacade {

    @Autowired
    private TransformationEngine transformationEngine;

    @Override
    public void transform(@NotNull File applicationFolder, @NotNull String templateId) {
        Application application = new Application(applicationFolder);
        Transformation transformation = new Transformation(application, templateId);

        transformationEngine.perform(transformation);
    }

}
