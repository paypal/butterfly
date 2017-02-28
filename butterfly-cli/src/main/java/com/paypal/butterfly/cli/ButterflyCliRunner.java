package com.paypal.butterfly.cli;

import com.paypal.butterfly.cli.logging.LogConfigurator;
import com.paypal.butterfly.cli.logging.LogFileDefiner;
import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.exception.ButterflyRuntimeException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.TransformationResult;
import com.paypal.butterfly.facade.exception.TemplateResolutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public int run() throws IOException {
        logger.info(ButterflyCliApp.getBanner());

        if (optionSet == null || optionSet.has(CLI_OPTION_HELP) || !optionSet.hasOptions()){
            logger.info("See CLI usage below\n");
            optionParser.printHelpOn(System.out);
            return 0;
        }

        if (optionSet.has(CLI_OPTION_VERBOSE)) {
            logConfigurator.verboseMode(true);
            logger.info("Verbose mode is ON");
        }

        if (optionSet.has(CLI_OPTION_DEBUG)) {
            logConfigurator.debugMode(true);
            logger.info("Debug mode is ON");
            logger.info("Butterfly home: {}", ButterflyCliApp.getButterflyHome());
        }

        if (optionSet.has(CLI_OPTION_LIST_EXTENSIONS)) {
            try {
                printExtensionsList(butterflyFacade);
                return 0;
            } catch (Exception e) {
                logger.error("An error when listing extensions has occurred", e);
                return 1;
            }
        }

        logger.info("");

        File applicationFolder = (File) optionSet.valueOf(CLI_OPTION_ORIGINAL_APP_FOLDER);
        if (!applicationFolder.exists()) {
            logger.error("This application folder does not exist: {}", applicationFolder.getAbsolutePath());
            return 1;
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
                logger.error("Invalid shortcut has been specified");
                return 1;
            }
            logger.info("Transformation template associated with shortcut {}: {}", shortcut, templateClass.getName());
        } else if (optionSet.has(CLI_OPTION_AUTOMATIC_TEMPLATE_RESOLUTION)) {
            try {
                templateClass = butterflyFacade.automaticResolution(applicationFolder);
                if (templateClass == null) {
                    logger.error("No transformation template could be resolved for this application");
                    return 1;
                }
                if (logger.isDebugEnabled()) {
                    logger.info("Transformation template automatically resolved");
                }
            } catch (TemplateResolutionException e) {
                logger.error(e.getMessage());
                return 1;
            }
        } else {
            logger.error("Transformation template class has not been specified");
            return 1;
        }

        if(createZip) {
            logger.info("-z option has been set, transformed application will be placed into a zip file");
        }

        Configuration configuration = new Configuration(transformedApplicationFolder, createZip);

        // Setting extensions log level to DEBUG
        if(optionSet.has(CLI_OPTION_DEBUG)) {
            List<Extension> registeredExtensions = butterflyFacade.getRegisteredExtensions();
            for(Extension extension : registeredExtensions) {
                logger.info("Setting DEBUG log level for extension {}", extension.getClass().getName());
                logConfigurator.setLoggerLevel(extension.getClass().getPackage().getName(), Level.DEBUG);
            }
        }

        try {
            if (templateClass == null) {
                templateClass = (Class<? extends TransformationTemplate>) Class.forName(templateClassName);
            }
            logger.info("Application to be transformed: {}", applicationFolder);
            logger.info("Transformation template class: {}", templateClass.getName());
            TransformationResult transformationResult = null;
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
            logger.info("");
            logger.info("----------------------------------------------");
            logger.info("Application has been transformed successfully!");
            logger.info("----------------------------------------------");
            logger.info("Transformed application folder: {}", transformationResult.getTransformedApplicationLocation());
            logger.info("Check log file for details: {}", LogFileDefiner.getLogFile());
            if (transformationResult.hasManualInstructions()) {
                logger.info("");
                logger.info(" **************************************************************************************");
                logger.info(" *** THIS APPLICATION REQUIRES POST-TRANSFORMATION MANUAL INSTRUCTIONS");
                logger.info(" *** Read manual instructions document for further details:");
                logger.info(" *** {}", transformationResult.getManualInstructionsFile());
                logger.info(" **************************************************************************************");
            }
            logger.info("");
        } catch (ButterflyException | ButterflyRuntimeException e) {
            logger.info("");
            logger.info("--------------------------------------------------------------------------------------------");
            logger.error("*** Transformation has been aborted due to:");
            logger.error("*** {}", e.getMessage());
            logger.info("--------------------------------------------------------------------------------------------");
            logger.info("Check log file for details: {}", LogFileDefiner.getLogFile().getAbsolutePath());
            return 1;
        } catch (ClassNotFoundException e) {
            logger.error("The specified transformation template class has not been found", e);
            return 1;
        }

        return 0;
    }

    private Class<? extends TransformationTemplate> getTemplateClass(int shortcut) {
        List<Extension> registeredExtensions = butterflyFacade.getRegisteredExtensions();

        if(registeredExtensions.size() == 0) {
            logger.info("There are no registered extensions");
            return null;
        }

        Extension extension;
        int shortcutCount = 1;
        for(Object extensionObj : registeredExtensions.toArray()) {
            extension = (Extension) extensionObj;
            for(Object templateObj : extension.getTemplateClasses().toArray()) {
                if (shortcutCount == shortcut) {
                    return (Class<? extends TransformationTemplate>) templateObj;
                }
                shortcutCount++;
            }
        }

        return null;
    }

    private static void printExtensionsList(ButterflyFacade butterflyFacade) throws IllegalAccessException, InstantiationException {
        List<Extension> registeredExtensions = butterflyFacade.getRegisteredExtensions();

        if(registeredExtensions.size() == 0) {
            logger.info("There are no registered extensions");
            return;
        }

        logger.info("See registered extensions below (shortcut in parenthesis)");

        Extension extension;
        Class<? extends TransformationTemplate> template;
        int shortcut = 1;
        for(Object extensionObj : registeredExtensions.toArray()) {
            extension = (Extension) extensionObj;
            System.out.printf("%n- %s: %s%n", extension, extension.getDescription());
            for(Object templateObj : extension.getTemplateClasses().toArray()) {
                template = (Class<? extends TransformationTemplate>) templateObj;
                System.out.printf("\t (%d) - [%s] \t %s \t %s%n", shortcut++, ExtensionTypeInitial.getFromClass(template), template.getName(), template.newInstance().getDescription());

            }
        }
    }

    private enum ExtensionTypeInitial {
        TT, US, VC;

        public static ExtensionTypeInitial getFromClass(Class<? extends TransformationTemplate> template) {
            if(UpgradeStep.class.isAssignableFrom(template)) return US;

            if(TransformationTemplate.class.isAssignableFrom(template)) return TT;

            // TODO
//            if(Validation.class.isAssignableFrom(template)) return VC;

            throw new IllegalArgumentException("Class " + template.getName() + " is not recognized as an extension type");
        }
    }

}