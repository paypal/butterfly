package com.paypal.butterfly.core;

import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.exception.TemplateResolutionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Butterfly Façade implementation
 *
 * @author facarvalho
 */
@Component
public class ButterflyFacadeImpl implements ButterflyFacade {

    private static final Logger logger = LoggerFactory.getLogger(ButterflyFacadeImpl.class);

    @Autowired
    private ExtensionRegistry extensionRegistry;

    @Autowired
    private TransformationEngine transformationEngine;

    @Autowired
    private CompressionHandler compressionHandler;

    @Override
    public List<Extension> getRegisteredExtensions() {
        return extensionRegistry.getExtensions();
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException {
        Set<Class<? extends TransformationTemplate>> resolvedTemplates = new HashSet<>();
        Class<? extends TransformationTemplate> t = null;

        for (Extension extension : extensionRegistry.getExtensions()) {
            t = extension.automaticResolution(applicationFolder);
            if (t != null) {
                resolvedTemplates.add(t);
            }
        }

        if (resolvedTemplates.size() == 0) {
            return null;
        }
        if (resolvedTemplates.size() == 1) {
            return (Class<? extends TransformationTemplate>) resolvedTemplates.toArray()[0];
        }

        throw new TemplateResolutionException(resolvedTemplates);
    }

    @Override
    public void transform(File applicationFolder, String templateClassName) throws ButterflyException {
        transform(applicationFolder, templateClassName, new Configuration());
    }

    @Override
    public void transform(File applicationFolder, String templateClassName, Configuration configuration) throws ButterflyException {
        if(StringUtils.isBlank(templateClassName)) {
            throw new IllegalArgumentException("Template class name cannot be blank");
        }
        try {
            Class<TransformationTemplate> templateClass = (Class<TransformationTemplate>) Class.forName(templateClassName);
            transform(applicationFolder, templateClass, configuration);
        } catch (ClassNotFoundException e) {
            String exceptionMessage = "Template class " + templateClassName + " not found. Run Butterfly in verbose mode and double check if its extension has been properly registered";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        }
    }

    @Override
    public void transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, Configuration configuration) throws ButterflyException {
        logger.debug("Transformation configuration: {}", configuration);

        Application application = new Application(applicationFolder);
        TransformationTemplate template = getTemplate(templateClass);
        Transformation transformation = new Transformation(application, template, configuration);

        transformationEngine.perform(transformation);

        if(configuration.isZipOutput()){
            compressionHandler.compress(transformation);
        }
    }

    private TransformationTemplate getTemplate(Class<? extends TransformationTemplate> templateClass) {
        if(templateClass == null) {
            throw new IllegalArgumentException("Template class cannot be null");
        }
        try {
            TransformationTemplate template = templateClass.newInstance();
            return template;
        } catch (InstantiationException e) {
            String exceptionMessage = "Template class " + templateClass + " could not be instantiated. Run Butterfly in verbose mode, double check if its extension has been properly registered, and also double check if it complies with Butterfly extensions API";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        } catch (IllegalAccessException e) {
            String exceptionMessage = "Template class " + templateClass + " could not be accessed";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        }
    }

}
