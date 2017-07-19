package com.paypal.butterfly.core;

import com.paypal.butterfly.core.exception.InternalException;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.TransformationResult;
import com.paypal.butterfly.facade.ButterflyProperties;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Butterfly Façade implementation
 *
 * @author facarvalho
 */
@Component
public class ButterflyFacadeImpl implements ButterflyFacade {

    private static final String VERSION = ButterflyProperties.getString("butterfly.version");

    private static final Logger logger = LoggerFactory.getLogger(ButterflyFacadeImpl.class);

    @Autowired
    private ExtensionRegistry extensionRegistry;

    @Autowired
    private TransformationEngine transformationEngine;

    @Autowired
    private CompressionHandler compressionHandler;

    @Override
    public String getButterflyVersion() {
        return VERSION;
    }

    @Override
    public Extension getRegisteredExtension() {
        return extensionRegistry.getExtension();
    }

    @Override
    public Class<? extends TransformationTemplate> automaticResolution(File applicationFolder) throws TemplateResolutionException {
        try {
            Extension extension = extensionRegistry.getExtension();
            if (extension == null) {
                throw new TemplateResolutionException("No Butterfly extension has been registered");
            }
            Class<? extends TransformationTemplate> chosenTemplate = extension.automaticResolution(applicationFolder);

            // Extension.automaticResolution must never return null. But still, just in case,
            // checking here if it does, and then throwing the exception, honoring the Extension and ButterflyFacade contract
            if (chosenTemplate == null) {
                throw new TemplateResolutionException("No transformation template could be chosen");
            }
            return chosenTemplate;
        } catch (IllegalStateException e) {
            throw new TemplateResolutionException("Multiple Butterfly extensions have been registered", e);
        }
    }

    @Override
    public TransformationResult transform(File applicationFolder, String templateClassName) throws ButterflyException {
        return transform(applicationFolder, templateClassName, new Configuration());
    }

    @Override
    public TransformationResult transform(File applicationFolder, String templateClassName, Configuration configuration) throws ButterflyException {
        if(StringUtils.isBlank(templateClassName)) {
            throw new IllegalArgumentException("Template class name cannot be blank");
        }
        try {
            Class<TransformationTemplate> templateClass = (Class<TransformationTemplate>) Class.forName(templateClassName);
            return transform(applicationFolder, templateClass, configuration);
        } catch (ClassNotFoundException e) {
            String exceptionMessage = "Template class " + templateClassName + " not found. Run Butterfly in debug mode and double check if its extension has been properly registered";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        }
    }

    @Override
    public TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass) throws ButterflyException {
        return transform(applicationFolder, templateClass, new Configuration());
    }

    @Override
    public TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, Configuration configuration) throws ButterflyException {
        TransformationTemplate template = getTemplate(templateClass);
        Application application = new Application(applicationFolder);
        Transformation transformation = new TemplateTransformation(application, template, configuration);

        return transform(transformation);
    }

    @Override
    public TransformationResult transform(File applicationFolder, UpgradePath upgradePath) throws ButterflyException {
        return transform(applicationFolder, upgradePath, new Configuration());
    }

    @Override
    public TransformationResult transform(File applicationFolder, UpgradePath upgradePath, Configuration configuration) throws ButterflyException {
        Application application = new Application(applicationFolder);
        Transformation transformation = new UpgradePathTransformation(application, upgradePath, configuration);

        return transform(transformation);
    }

    private TransformationResult transform(Transformation transformation) throws ButterflyException {
        Configuration configuration = transformation.getConfiguration();
        if (logger.isDebugEnabled()) {
            logger.debug("Transformation configuration: {}", configuration);
        }

        TransformationResult transformationResult = transformationEngine.perform(transformation);

        if(configuration.isZipOutput()){
            compressionHandler.compress(transformation);
        }

        return transformationResult;
    }

    private TransformationTemplate getTemplate(Class<? extends TransformationTemplate> templateClass) {
        if(templateClass == null) {
            throw new IllegalArgumentException("Template class cannot be null");
        }
        try {
            TransformationTemplate template = templateClass.newInstance();
            return template;
        } catch (InstantiationException e) {
            String exceptionMessage = "Template class " + templateClass + " could not be instantiated. Run Butterfly in debug mode, double check if its extension has been properly registered, and also double check if it complies with Butterfly extensions API";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        } catch (IllegalAccessException e) {
            String exceptionMessage = "Template class " + templateClass + " could not be accessed";
            logger.error(exceptionMessage, e);
            throw new InternalException(exceptionMessage, e);
        }
    }

}
