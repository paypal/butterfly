package com.paypal.butterfly.cli;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradePath;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
import com.paypal.butterfly.facade.exception.TemplateResolutionException;
import com.paypal.butterfly.facade.exception.TransformationException;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Butterfly Command Line Interface
 *
 * @author facarvalho
 */
@Component
public class ButterflyCli {

    @Autowired
    private VerboseConfigurator verboseConfigurator;

    @Autowired
    private ButterflyFacade butterflyFacade;

    private static final String BANNER = String.format("Butterfly application transformation tool (version %s)", VersionHelper.getButterflyVersion());

    // Possibe CLI options. See method createOptionSet for details about them
    private static final String CLI_OPTION_HELP = "h";
    private static final String CLI_OPTION_VERBOSE = "v";
    private static final String CLI_OPTION_LIST_EXTENSIONS = "l";
    private static final String CLI_OPTION_ORIGINAL_APP_FOLDER = "i";
    private static final String CLI_OPTION_TRANSFORMED_APP_FOLDER = "o";
    private static final String CLI_OPTION_TEMPLATE = "t";
    private static final String CLI_OPTION_CREATE_ZIP = "z";
    private static final String CLI_OPTION_TEMPLATE_SHORTCUT = "s";
    private static final String CLI_OPTION_AUTOMATIC_TEMPLATE_RESOLUTION = "a";
    private static final String CLI_OPTION_UPGRADE_VERSION = "u";

    private static Logger logger = LoggerFactory.getLogger(ButterflyCli.class);

    public int run(String... arguments) throws IOException {
        logger.info(BANNER);

        OptionParser optionParser = createOptionSet();
        OptionSet optionSet = null;

        if(arguments.length != 0){
            try {
                optionSet = optionParser.parse(arguments);
            } catch (OptionException e) {
                logger.error(e.getMessage());
                return 1;
            }
        }

        if(optionSet != null && optionSet.has(CLI_OPTION_VERBOSE)){
            verboseConfigurator.verboseMode(true);
            logger.debug("Verbose mode is ON");
        }

        if(optionSet == null || optionSet.has(CLI_OPTION_HELP) || !optionSet.hasOptions()){
            logger.info("See CLI usage below\n");
            optionParser.printHelpOn(System.out);
            return 0;
        }

        if(optionSet.has(CLI_OPTION_LIST_EXTENSIONS)) {
            try {
                printExtensionsList(butterflyFacade);
                return 0;
            } catch (Exception e) {
                logger.error("An error when listing extensions has occurred", e);
                return 1;
            }
        }

        File applicationFolder = (File) optionSet.valueOf(CLI_OPTION_ORIGINAL_APP_FOLDER);
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
                logger.info("Transformation template automatically resolved");
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
        if(optionSet.has(CLI_OPTION_VERBOSE)) {
            List<Extension> registeredExtensions = butterflyFacade.getRegisteredExtensions();
            for(Extension extension : registeredExtensions) {
                logger.debug("Setting DEBUG log level for extension {}", extension.getClass().getName());
                verboseConfigurator.setLoggerLevel(extension.getClass().getPackage().getName(), Level.DEBUG);
            }
        }

        try {
            if (templateClass == null) {
                templateClass = (Class<? extends TransformationTemplate>) Class.forName(templateClassName);
            }
            logger.info("Transformation template class: \t{}", templateClass.getName());
            if (UpgradeStep.class.isAssignableFrom(templateClass)) {
                Class<? extends UpgradeStep> firstStepClass = (Class<? extends UpgradeStep>) templateClass;
                String upgradeVersion = (String) optionSet.valueOf(CLI_OPTION_UPGRADE_VERSION);
                UpgradePath upgradePath = new UpgradePath(firstStepClass, upgradeVersion);
                butterflyFacade.transform(applicationFolder, upgradePath, configuration);
            } else {
                butterflyFacade.transform(applicationFolder, templateClass, configuration);
            }
            logger.info("Application has been transformed");
        } catch (TransformationException e) {
            logger.error("A transformation error has occurred", e);
            return 1;
        } catch (ButterflyException e) {
            logger.error("An error has occurred", e);
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

    private static OptionParser createOptionSet() {
        OptionParser optionParser = new OptionParser();

        // Help option
        optionParser.acceptsAll(asList(CLI_OPTION_HELP, "?"), "Show this help")
                .forHelp();

        // List extensions option
        optionParser.accepts(CLI_OPTION_LIST_EXTENSIONS, "List all registered extensions");

        // Application folder option
        optionParser.accepts(CLI_OPTION_ORIGINAL_APP_FOLDER, "The folder location in the file system where the application to be transformed is")
                .requiredUnless(CLI_OPTION_LIST_EXTENSIONS)
                .withRequiredArg()
                .ofType(File.class)
                .describedAs("input");

        // Transformation template shortcut option
        optionParser.accepts(CLI_OPTION_TEMPLATE_SHORTCUT, "The shortcut number to the transformation template to be executed. If both shortcut (-s) and template class (-t) name are supplied, the shortcut will be ignored. If the chosen transformation template is an upgrade template, then the application will be upgraded all the way to the latest version possible, unless upgrade version (-u) is specified")
                .withRequiredArg()
                .ofType(Integer.class)
                .describedAs("template shortcut");

        // Automatic transformation template resolution option
        optionParser.accepts(CLI_OPTION_AUTOMATIC_TEMPLATE_RESOLUTION, "If provided, Butterfly will try to automatically chose the transformation template to be used based on the application code. If shortcut (-s) or template class name (-t) are also supplied, this option (-a) will be ignored. If the chosen transformation template is an upgrade template, then the application will be upgraded all the way to the latest version possible, unless upgrade version (-u) is specified");

        // Transformation template option
        optionParser.accepts(CLI_OPTION_TEMPLATE, "The Java class name of the transformation template to be executed. This option has precedence over -s and -a. If the chosen transformation template is an upgrade template, then the application will be upgraded all the way to the latest version possible, unless upgrade version (-u) is specified")
                .requiredUnless(CLI_OPTION_LIST_EXTENSIONS, CLI_OPTION_TEMPLATE_SHORTCUT, CLI_OPTION_AUTOMATIC_TEMPLATE_RESOLUTION)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("template");

        // Upgrade version option
        optionParser.accepts(CLI_OPTION_UPGRADE_VERSION, "The version the application should be upgraded to. This option only makes sense if the transformation template to be used is also an upgrade template. If not, it is ignored. If it is, but this option is not specified, then the application will be upgraded all the way to the latest version possible")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("upgrade version");

        // Transformed application folder option
        optionParser.accepts(CLI_OPTION_TRANSFORMED_APP_FOLDER, "The folder location in the file system where the transformed application should be placed. It defaults to same location where original application is. Transformed application is placed under a new folder whose name is same as original folder, plus \"-transformed-yyyyMMddHHmmssSSS\" suffix")
                .withRequiredArg()
                .ofType(File.class)
                .describedAs("output");

        // Verbose option
        optionParser.accepts(CLI_OPTION_VERBOSE, "Runs Butterfly in verbose mode");

        // Create Zip option
        optionParser.accepts(CLI_OPTION_CREATE_ZIP, "Outputs a zip file instead of a folder");

        return optionParser;
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