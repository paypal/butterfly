package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.facade.ButterflyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Set;

/**
 * Buttefly Façade implementation
 *
 * @author facarvalho
 */
@Component
public class ButteflyFacadeImpl implements ButterflyFacade {

    @Autowired
    private ExtensionRegistry extensionRegistry;

    @Autowired
    private TransformationEngine transformationEngine;

    @Override
    public Set<Extension> getRegisteredExtensions() {
        return extensionRegistry.getExtensions();
    }

    @Override
    public void transform(@NotNull File applicationFolder, @NotNull String templateClassName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Application application = new Application(applicationFolder);
        Class<TransformationTemplate> templateClass = (Class<TransformationTemplate>) Class.forName(templateClassName);
        TransformationTemplate template = (TransformationTemplate) templateClass.newInstance();
        Transformation transformation = new Transformation(application, template);

        transformationEngine.perform(transformation);
    }

}
