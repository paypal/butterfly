package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.core.exception.InternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
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
    public Set<Extension> getRegisteredExtensions() {
        return extensionRegistry.getExtensions();
    }

    @Override
    public void transform(@NotNull File applicationFolder, @NotNull String templateClassName) throws ButterflyException {
        transform(applicationFolder, templateClassName, new Configuration());
    }

    @Override
    public void transform(File applicationFolder, String templateClassName, Configuration configuration) throws ButterflyException {
        logger.debug("Transformation configuration: {}", configuration);

        if(!applicationFolder.exists() || applicationFolder.isFile()) {
            throw new IllegalArgumentException("Invalid application folder (" + applicationFolder.getAbsolutePath() + ")");
        }

        Application application = new Application(applicationFolder);
        TransformationTemplate template = getTemplate(templateClassName);
        Transformation transformation = new Transformation(application, template, configuration);

        transformationEngine.perform(transformation);

        if(configuration.isZipOutput()){
            compressionHandler.compress(transformation);
        }
    }

    private TransformationTemplate getTemplate(String templateClassName) {
        try {
            Class<TransformationTemplate> templateClass = (Class<TransformationTemplate>) Class.forName(templateClassName);
            TransformationTemplate template = (TransformationTemplate) templateClass.newInstance();

            return template;
        } catch (ClassNotFoundException e) {
            String exceptionMessage = "Template class " + templateClassName + " not found. Run Butterfly in verbose mode and double check if its extension has been properly registered";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        } catch (InstantiationException e) {
            String exceptionMessage = "Template class " + templateClassName + " could not be instantiated. Run Butterfly in verbose mode, double check if its extension has been properly registered, and also double check if it complies with Butterfly extensions API";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        } catch (IllegalAccessException e) {
            String exceptionMessage = "Template class " + templateClassName + " could not be accessed";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        }
    }

}
