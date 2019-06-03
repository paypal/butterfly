package com.paypal.butterfly.core;

import com.paypal.butterfly.api.*;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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
    public List<Extension> getExtensions() {
        return extensionRegistry.getExtensions();
    }

    @Override
    public Optional<Class<? extends TransformationTemplate>> automaticResolution(File applicationFolder) throws TemplateResolutionException {
        if (extensionRegistry.getExtensions().isEmpty()) {
            throw new TemplateResolutionException("No Butterfly extension has been registered");
        }

        Set<Class<? extends TransformationTemplate>> resolvedTemplates = new HashSet<>();
        Optional<Class<? extends TransformationTemplate>> t;
        Map<String, TemplateResolutionException> extensionsResolutionExceptions = new HashMap();

        for (Extension extension : extensionRegistry.getExtensions()) {
            try {
                t = extension.automaticResolution(applicationFolder);
                if (t.isPresent()) {
                    resolvedTemplates.add(t.get());
                }
            } catch (TemplateResolutionException e) {
                extensionsResolutionExceptions.put(extension.getClass().getName(), e);
            }
        }

        if (resolvedTemplates.size() == 1) {
            return Optional.of((Class<? extends TransformationTemplate>) resolvedTemplates.toArray()[0]);
        }
        if (resolvedTemplates.size() > 1) {
            throw new TemplateResolutionException("More than one transformation template was resolved, they are: " + resolvedTemplates);
        }
        if (extensionsResolutionExceptions.size() == 1) {
            throw (TemplateResolutionException) extensionsResolutionExceptions.values().toArray()[0];
        } else if (extensionsResolutionExceptions.size() > 1) {
            throw new TemplateResolutionException("No transformation template could be resolved. However, more than one extension recognized the application type, but considered them invalid. See the following map, whose key is an extension class, and value is the reason why application failed validation: " + extensionsResolutionExceptions);
        }
        return Optional.empty();
    }

    @Override
    public Configuration newConfiguration(Properties properties) {
        return new ConfigurationImpl(properties);
    }

    @Override
    public Configuration newConfiguration(Properties properties, boolean zipOutput) {
        return new ConfigurationImpl(properties, zipOutput);
    }

    @Override
    public Configuration newConfiguration(Properties properties, File outputFolder, boolean zipOutput) {
        return new ConfigurationImpl(properties, outputFolder, zipOutput);
    }

    @Override
    public CompletableFuture<TransformationResult> transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass) {
        return transform(applicationFolder, templateClass, null, new ConfigurationImpl(null));
    }

    @Override
    public CompletableFuture<TransformationResult> transform(File applicationFolder, Class<? extends TransformationTemplate> templateClass, String version, Configuration configuration) {
        TransformationTemplate template = getTemplate(templateClass);
        Application application = new ApplicationImpl(applicationFolder);
        TransformationRequest transformationRequest;

        if (template instanceof UpgradeStep) {
            UpgradePath upgradePath = getUpgradePath(templateClass, version);
            transformationRequest = new UpgradePathTransformationRequest(application, upgradePath, configuration);
        } else {
            transformationRequest = new TemplateTransformationRequest(application, template, configuration);
        }

        return transform(transformationRequest);
    }

    private UpgradePath getUpgradePath(Class<? extends TransformationTemplate> transformationTemplate, String version) {
        Class<? extends UpgradeStep> upgradeStep = (Class<? extends UpgradeStep>) transformationTemplate;
        UpgradePath upgradePath;
        if (version != null && !version.trim().equals("")) {
            upgradePath = new UpgradePath(upgradeStep, version);
        } else {
            upgradePath = new UpgradePath(upgradeStep);
        }

        return upgradePath;
    }

    private CompletableFuture<TransformationResult> transform(TransformationRequest transformationRequest) {
        Configuration configuration = transformationRequest.getConfiguration();
        if (logger.isDebugEnabled()) {
            logger.debug("Transformation request configuration: {}", configuration);
        }

        CompletableFuture<TransformationResult> transformationResult = transformationEngine.perform(transformationRequest);

        if(!configuration.isModifyOriginalFolder() && configuration.isZipOutput()){
            transformationResult.thenAcceptAsync(compressionHandler::compress);
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
            throw new InternalException(exceptionMessage, e);
        } catch (IllegalAccessException e) {
            String exceptionMessage = "Template class " + templateClass + " could not be accessed";
            throw new InternalException(exceptionMessage, e);
        }
    }

}
