package com.paypal.butterfly.core;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.paypal.butterfly.api.*;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;

/**
 * Butterfly Façade implementation
 *
 * @author facarvalho
 */
@Component
class ButterflyFacadeImpl implements ButterflyFacade {

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
    public Configuration newConfiguration() {
        return new ConfigurationImpl();
    }

    @Override
    public Configuration newConfiguration(boolean zipOutput) {
        return new ConfigurationImpl(zipOutput);
    }

    @Override
    public Configuration newConfiguration(File outputFolder, boolean zipOutput) {
        return new ConfigurationImpl(outputFolder, zipOutput);
    }

    @Override
    public TransformationResult transform(File applicationFolder, String templateClassName) throws ButterflyException {
        return transform(applicationFolder, templateClassName, new ConfigurationImpl());
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
            String exceptionMessage = "Template class " + templateClassName + " not found, double check if its extension has been properly registered";
            throw new ButterflyException(exceptionMessage, e);
        }
    }

    @Override
    public TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass) {
        return transform(applicationFolder, templateClass, new ConfigurationImpl());
    }

    @Override
    public TransformationResult transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, Configuration configuration) {
        TransformationTemplate template = getTemplate(templateClass);
        Application application = new ApplicationImpl(applicationFolder);
        TransformationRequest transformationRequest = new TemplateTransformationRequest(application, template, configuration);

        return transform(transformationRequest);
    }

    @Override
    public TransformationResult transform(File applicationFolder, UpgradePath upgradePath) {
        return transform(applicationFolder, upgradePath, new ConfigurationImpl());
    }

    @Override
    public TransformationResult transform(File applicationFolder, UpgradePath upgradePath, Configuration configuration) {
        Application application = new ApplicationImpl(applicationFolder);
        TransformationRequest transformationRequest = new UpgradePathTransformationRequest(application, upgradePath, configuration);

        return transform(transformationRequest);
    }

    private TransformationResult transform(TransformationRequest transformationRequest) {
        Configuration configuration = transformationRequest.getConfiguration();
        if (logger.isDebugEnabled()) {
            logger.debug("Transformation request configuration: {}", configuration);
        }

        TransformationResult transformationResult = transformationEngine.perform(transformationRequest);

        if(!configuration.isModifyOriginalFolder() && configuration.isZipOutput()){
            compressionHandler.compress(transformationResult);
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
