package com.paypal.butterfly.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.api.Configuration;
import com.paypal.butterfly.api.TransformationResult;
import com.paypal.butterfly.cli.logging.LogConfigurator;
import com.paypal.butterfly.cli.logging.LogFileDefiner;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyRuntimeException;
import com.paypal.butterfly.extensions.api.exception.TemplateResolutionException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;

/**
 * Butterfly CLI runner
 *
 * @author facarvalho
 */
@Component
public class ButterflyCliRunner extends ButterflyCliOption {

    @Autowired
    private LogConfigurator logConfigurator;

    @Autowired
    private ButterflyFacade butterflyFacade;

    private static final Logger logger = LoggerFactory.getLogger(ButterflyCliRunner.class);

    public ButterflyCliRun run() throws IOException {
        ButterflyCliRun run = new ButterflyCliRun();
        Configuration configuration;
        run.setButterflyVersion(butterflyFacade.getButterflyVersion());

        logger.info(ButterflyCliApp.getBanner());

        if (optionSet == null || optionSet.has(CLI_OPTION_HELP) || (!optionSet.hasOptions() && optionSet.nonOptionArguments() == null)){
            logger.info("");
            logger.info("Usage:\t butterfly [options] [application folder]");
            logger.info("");
            logger.info("The following options are available:\n");
            optionParser.printHelpOn(System.out);
            run.setExitStatus(0);
            return run;
        }

        if (optionSet.has(CLI_OPTION_VERBOSE)) {
            logConfigurator.setVerboseMode(true);
            logger.info("Verbose mode is ON");
        }

        if (optionSet.has(CLI_OPTION_DEBUG)) {
            logConfigurator.setDebugMode(true);
            logger.info("Debug mode is ON");
            logger.info("Butterfly home: {}", ButterflyCliApp.getButterflyHome());
            logger.info("JAVA_HOME: {}", System.getenv("JAVA_HOME"));
            logger.info("java.version: {}", System.getProperty("java.version"));
            logger.info("java.runtime.version: {}", System.getProperty("java.runtime.version"));
            logger.info("M2_HOME: {}", System.getenv("M2_HOME"));
        }

        if (optionSet.has(CLI_OPTION_LIST_EXTENSIONS)) {
            try {
                ExtensionMetaData extensionMetaData = getExtensionsMetaData(butterflyFacade);
                if (extensionMetaData != null) {
                    run.addExtensionMetaData(extensionMetaData);
                }
                printExtensionMetaData(extensionMetaData);
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
                templateClass = butterflyFacade.automaticResolution(applicationFolder);
                if (templateClass == null) {
                    registerError(run, "No transformation template could be resolved for this application. Specify it explicitly using option -t or -s.");
                    return run;
                }
                logger.info("Transformation template automatically resolved");
            } catch (TemplateResolutionException e) {
                registerError(run, e.getMessage());
                return run;
            }
        }

        if(createZip) {
            logger.info("-z option has been set, transformed application will be placed into a zip file");
        }

        if (optionSet.has(CLI_OPTION_MODIFY_ORIGINAL_FOLDER)) {
            configuration = butterflyFacade.newConfiguration();
        } else if (transformedApplicationFolder == null) {
            configuration = butterflyFacade.newConfiguration(createZip);
        } else {
            configuration = butterflyFacade.newConfiguration(transformedApplicationFolder, createZip);
        }

        // Setting extensions log level to DEBUG
        if(optionSet.has(CLI_OPTION_DEBUG)) {
            Extension extension = butterflyFacade.getRegisteredExtension();
            if (extension != null) {
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
                UpgradePath upgradePath = new UpgradePath(firstStepClass, upgradeVersion);

                logger.info("Performing upgrade from version {} to version {} (it might take a few seconds)", upgradePath.getOriginalVersion(), upgradePath.getUpgradeVersion());
                transformationResult = butterflyFacade.transform(applicationFolder, upgradePath, configuration);
            } else {
                logger.info("Performing transformation (it might take a few seconds)");
                transformationResult = butterflyFacade.transform(applicationFolder, templateClass, configuration);
            }

            run.setLogFile(LogFileDefiner.getLogFile());

            if (transformationResult.isSuccessful()) {
                logger.info("");
                logger.info("----------------------------------------------");
                logger.info("Application has been transformed successfully!");
                logger.info("----------------------------------------------");
                logger.info("Transformed application folder: {}", transformationResult.getTransformedApplicationDir());
                logger.info("Check log file for details: {}", LogFileDefiner.getLogFile().getAbsolutePath());

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

    private void transformationAbort(ButterflyCliRun run, String abortMessage) {
        logger.info("");
        logger.info("--------------------------------------------------------------------------------------------");
        logger.error("*** Transformation has been aborted due to:");
        logger.error("*** {}", abortMessage);
        logger.info("--------------------------------------------------------------------------------------------");
        logger.info("Check log file for details: {}", LogFileDefiner.getLogFile().getAbsolutePath());

        run.setErrorMessage("Transformation has been aborted due to: " + abortMessage);
        run.setExceptionMessage(abortMessage);
        run.setExitStatus(1);
    }

    private Class<? extends TransformationTemplate> getTemplateClass(int shortcut) {
        Extension extension = butterflyFacade.getRegisteredExtension();

        if(extension == null) {
            logger.info("There are no registered extensions");
            return null;
        }

        int shortcutCount = 1;
        for(Object templateObj : extension.getTemplateClasses().toArray()) {
            if (shortcutCount == shortcut) {
                return (Class<? extends TransformationTemplate>) templateObj;
            }
            shortcutCount++;
        }

        return null;
    }

    private ExtensionMetaData getExtensionsMetaData(ButterflyFacade butterflyFacade) throws IllegalAccessException, InstantiationException {
        Extension extension = butterflyFacade.getRegisteredExtension();
        if(extension == null) {
            return null;
        }
        return ExtensionMetaData.newExtensionMetaData(extension);
    }

    private void printExtensionMetaData(ExtensionMetaData extensionMetaData) {
        if(extensionMetaData == null) {
            logger.info("There are no registered extensions");
            return;
        }

        String version = (StringUtils.isEmpty(extensionMetaData.getVersion()) ? "" : String.format("(version %s)", extensionMetaData.getVersion()));

        logger.info("See registered extensions below (shortcut in parenthesis)");
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