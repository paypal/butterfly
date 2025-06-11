package com.paypal.butterfly.cli;

import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.api.Configuration;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.cli.logging.LogConfigurator;
import com.paypal.butterfly.cli.logging.LogFileDefinition;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyRuntimeException;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Butterfly CLI runner
 *
 * @author facarvalho
 */
@Component
class ButterflyCliRunner extends ButterflyCliOption {

    @Autowired
    private LogConfigurator logConfigurator;

    @Autowired
    private ButterflyFacade butterflyFacade;

    private static final Logger logger = LoggerFactory.getLogger(ButterflyCliRunner.class);

    ButterflyCliRun run(File butterflyHome, String butterflyBanner) {
        ButterflyCliRun run = new ButterflyCliRun();
        Configuration configuration;
        run.setButterflyVersion(butterflyFacade.getButterflyVersion());

        logger.info(butterflyBanner);

        if (optionSet == null || optionSet.has(CLI_OPTION_HELP) || (!optionSet.hasOptions() && optionSet.nonOptionArguments() == null)){
            logger.info("");
            logger.info("Usage:\t butterfly [options] [application folder]");
            logger.info("");
            logger.info("The following options are available:\n");
            try {
                optionParser.printHelpOn(System.out);
                run.setExitStatus(0);
                return run;
            } catch (IOException e) {
                registerError(run, "An error occurred when printing help", e);
                return run;
            }
        }

        if (optionSet.has(CLI_OPTION_VERBOSE)) {
            logConfigurator.setVerboseMode(true);
            logger.info("Verbose mode is ON");
        }

        if (optionSet.has(CLI_OPTION_DEBUG)) {
            logConfigurator.setDebugMode(true);
            logger.info("Debug mode is ON");
            logger.info("Butterfly home: {}", butterflyHome);
            logger.info("JAVA_HOME: {}", System.getenv("JAVA_HOME"));
            logger.info("java.version: {}", System.getProperty("java.version"));
            logger.info("java.runtime.version: {}", System.getProperty("java.runtime.version"));
            logger.info("M2_HOME: {}", System.getenv("M2_HOME"));
        }

        if (optionSet.has(CLI_OPTION_LIST_EXTENSIONS)) {
            try {
                if (butterflyFacade.getExtensions().isEmpty()) {
                    logger.info("There are no registered extensions");
                } else {
                    logger.info("See registered extensions below (shortcut in parenthesis)");
                    for (Extension e : butterflyFacade.getExtensions()) {
                        ExtensionMetaData extensionMetaData = ExtensionMetaData.newExtensionMetaData(e);
                        run.addExtensionMetaData(extensionMetaData);
                        printExtensionMetaData(extensionMetaData);
                    }
                }

                run.setExitStatus(0);
                return run;
            } catch (Throwable e) {
                registerError(run, "An error occurred when listing extensions", e);
                return run;
            }
        }

        logger.info("");

        // Setting application folder
        List<?> nonOptionArguments = optionSet.nonOptionArguments();
        if (nonOptionArguments == null || nonOptionArguments.size() == 0 || StringUtils.isEmpty(nonOptionArguments.get(0))) {
            registerError(run, "Application folder has not been specified");
            return run;
        }
        File applicationFolder = new File((String) nonOptionArguments.get(0));
        if (!applicationFolder.exists()) {
            String errorMessage = String.format("This application folder does not exist: %s", applicationFolder.getAbsolutePath());
            registerError(run, errorMessage);
            return run;
        }

        File transformedApplicationFolder = (File) optionSet.valueOf(CLI_OPTION_TRANSFORMED_APP_FOLDER);

        boolean createZip = optionSet.has(CLI_OPTION_CREATE_ZIP);

        String templateClassName = null;
        Class<? extends TransformationTemplate> templateClass = null;

        if (optionSet.has(CLI_OPTION_TEMPLATE)) {
            templateClassName = (String) optionSet.valueOf(CLI_OPTION_TEMPLATE);
        } else if (optionSet.has(CLI_OPTION_TEMPLATE_SHORTCUT)) {
            int shortcut = (Integer) optionSet.valueOf(CLI_OPTION_TEMPLATE_SHORTCUT);
            templateClass = getTemplateClass(shortcut);
            if (templateClass == null) {
                registerError(run, "Invalid shortcut has been specified");
                return run;
            }
            logger.info("Transformation template associated with shortcut {}: {}", shortcut, templateClass.getName());
        } else {
            try {
                Optional<Class<? extends TransformationTemplate>> resolution = butterflyFacade.automaticResolution(applicationFolder);
                if (!resolution.isPresent()) {
                    registerError(run, "No transformation template could be resolved for this application. Specify it explicitly using option -t or -s.");
                    return run;
                }
                templateClass = resolution.get();
                logger.info("Transformation template automatically resolved");
            } catch (TemplateResolutionException e) {
                registerError(run, e.getMessage());
                return run;
            }
        }

        if(createZip) {
            logger.info("-z option has been set, transformed application will be placed into a zip file");
        }

        // Setting transformation specific properties
        Properties properties = new Properties();
        try {
            if (optionSet.has(CLI_OPTION_INLINE_PROPERTIES)) {
                String inlineProperties = (String) optionSet.valueOf(CLI_OPTION_INLINE_PROPERTIES);
                // Regex to ignore escaped semi-colons 
                try (StringReader stringReader = new StringReader(inlineProperties.replaceAll("(?<!\\\\);", "\n"))) {
                    properties.load(stringReader);
                }
            } else if (optionSet.has(CLI_OPTION_PROPERTIES_FILE)) {
                File propertiesFile = (File) optionSet.valueOf(CLI_OPTION_PROPERTIES_FILE);
                try (FileInputStream fileInputStream = new FileInputStream(propertiesFile)) {
                    properties.load(fileInputStream);
                }
            }
        } catch (Exception e) {
            registerError(run, "Error when reading or parsing the specified properties", e);
            return run;
        }

        if (optionSet.has(CLI_OPTION_MODIFY_ORIGINAL_FOLDER)) {
            configuration = butterflyFacade.newConfiguration(properties);
        } else if (transformedApplicationFolder == null) {
            configuration = butterflyFacade.newConfiguration(properties, createZip);
        } else {
            configuration = butterflyFacade.newConfiguration(properties, transformedApplicationFolder, createZip);
        }

        // Setting extensions log level to DEBUG
        if(optionSet.has(CLI_OPTION_DEBUG)) {
            List<Extension> registeredExtensions = butterflyFacade.getExtensions();
            for(Extension extension : registeredExtensions) {
                logger.info("Setting DEBUG log level for extension {}", extension.getClass().getName());
                logConfigurator.setLoggerLevel(extension.getClass().getPackage().getName(), Level.DEBUG);
            }
        }

        TransformationResult transformationResult = null;

        try {
            if (templateClass == null) {
                templateClass = (Class<? extends TransformationTemplate>) Class.forName(templateClassName);
            }

            if (UpgradeStep.class.isAssignableFrom(templateClass)) {
                Class<? extends UpgradeStep> firstStepClass = (Class<? extends UpgradeStep>) templateClass;
                String upgradeVersion = (String) optionSet.valueOf(CLI_OPTION_UPGRADE_VERSION);
                String originalVersion = firstStepClass.newInstance().getCurrentVersion();

                logger.info("Performing upgrade from version {} to version {} (it might take a few seconds)",
                        originalVersion, Optional.ofNullable(upgradeVersion).orElse("LATEST"));
                transformationResult = butterflyFacade.transform(applicationFolder, firstStepClass, upgradeVersion, configuration).get();
            } else {
                logger.info("Performing transformation (it might take a few seconds)");
                transformationResult = butterflyFacade.transform(applicationFolder, templateClass, null, configuration).get();
            }

            run.setLogFile(getLogFile());

            if (transformationResult.isSuccessful()) {
                logger.info("");
                logger.info("----------------------------------------------");
                logger.info("Application has been transformed successfully!");
                logger.info("----------------------------------------------");
                logger.info("Transformed application folder: {}", transformationResult.getTransformedApplicationDir());
                logger.info("Check log file for details: {}", getLogFile().getAbsolutePath());

                if (transformationResult.hasManualInstructions()) {
                    logger.info("");
                    logger.info(" **************************************************************************************");
                    logger.info(" *** THIS APPLICATION REQUIRES POST-TRANSFORMATION MANUAL INSTRUCTIONS");
                    logger.info(" *** Read manual instructions document for further details:");
                    logger.info(" *** {}", transformationResult.getManualInstructionsFile());
                    logger.info(" **************************************************************************************");
                }
                logger.info("");
            } else {
                transformationAbort(run, transformationResult.getAbortDetails().getAbortMessage());
            }
        } catch (ButterflyRuntimeException e) {
            transformationAbort(run, e.getMessage());
        } catch (ClassNotFoundException e) {
            registerError(run, "The specified transformation template class has not been found", e);
        } catch (IllegalArgumentException e) {
            registerError(run, "This transformation request input arguments are invalid", e);
        } catch (Throwable e) {
            registerError(run, "An error happened when processing this transformation request", e);
        } finally {
            run.setTransformationResult(transformationResult);
        }

        return run;
    }

    private static File getLogFile() {
        return LogFileDefinition.getInstance().getLogFile();
    }

    private void transformationAbort(ButterflyCliRun run, String abortMessage) {
        logger.info("");
        logger.info("--------------------------------------------------------------------------------------------");
        logger.error("*** Transformation has been aborted due to:");
        logger.error("*** {}", abortMessage);
        logger.info("--------------------------------------------------------------------------------------------");
        if (getLogFile() != null) {
            logger.info("Check log file for details: {}", getLogFile().getAbsoluteFile());
        }

        run.setErrorMessage("Transformation has been aborted due to: " + abortMessage);
        run.setExceptionMessage(abortMessage);
        run.setExitStatus(1);
    }

    private Class<? extends TransformationTemplate> getTemplateClass(int shortcut) {
        List<Extension> registeredExtensions = butterflyFacade.getExtensions();

        if(registeredExtensions.size() == 0) {
            logger.info("There are no registered extensions");
            return null;
        }

        int shortcutCount = 1;
        for(Extension extension : registeredExtensions) {
            for (Object templateObj : extension.getTemplateClasses()) {
                if (shortcutCount == shortcut) {
                    return (Class<? extends TransformationTemplate>) templateObj;
                }
                shortcutCount++;
            }
        }

        return null;
    }

    private void printExtensionMetaData(ExtensionMetaData extensionMetaData) {
        String version = (StringUtils.isEmpty(extensionMetaData.getVersion()) ? "" : String.format("(version %s)", extensionMetaData.getVersion()));

        logger.info("");
        logger.info("- {}: {} {}", extensionMetaData.getName(), extensionMetaData.getDescription(), version);
        extensionMetaData.getTemplates().forEach(t -> logger.info("\t ({}) - [{}] \t {} \t {}", t.getShortcut(), t.getTemplateType().getInitials(), t.getName(), t.getDescription()));
    }

    private void registerError(ButterflyCliRun run, String errorMessage) {
        registerError(run, errorMessage, null);
    }

    private void registerError(ButterflyCliRun run, String errorMessage, Throwable throwable) {
        if (throwable == null || !logConfigurator.isVerboseMode()) {
            logger.error(errorMessage);
        } else {
            logger.error(errorMessage, throwable);
        }
        if (throwable != null) {
            run.setExceptionMessage(throwable.getMessage());
        }
        run.setErrorMessage(errorMessage);
        run.setExitStatus(1);
    }

}
