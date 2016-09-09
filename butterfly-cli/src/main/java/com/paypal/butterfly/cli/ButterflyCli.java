package com.paypal.butterfly.cli;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.extensions.api.exception.ButterflyException;
import com.paypal.butterfly.extensions.api.upgrade.UpgradeStep;
import com.paypal.butterfly.facade.ButterflyFacade;
import com.paypal.butterfly.facade.Configuration;
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
import java.util.Set;

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

    private static final String CLI_OPTION_HELP = "h";
    private static final String CLI_OPTION_VERBOSE = "v";
    private static final String CLI_OPTION_LIST_EXTENSIONS = "l";
    private static final String CLI_OPTION_ORIGINAL_APP_FOLDER = "i";
    private static final String CLI_OPTION_TRANSFORMED_APP_FOLDER = "o";
    private static final String CLI_OPTION_TEMPLATE = "t";
    private static final String CLI_OPTION_CREATE_ZIP = "z";

    private static Logger logger = LoggerFactory.getLogger(ButterflyCli.class);

    public void run(String... arguments) throws IOException {
        logger.info(BANNER);

        OptionParser optionParser = createOptionSet();
        OptionSet optionSet = null;

        if(arguments.length != 0){
            try {
                optionSet = optionParser.parse(arguments);
            } catch (OptionException e) {
                logger.error(e.getMessage());
                System.exit(1);
            }
        }

        if(optionSet != null && optionSet.has(CLI_OPTION_VERBOSE)){
            verboseConfigurator.verboseMode(true);
            logger.debug("Verbose mode is ON");
        }

        if(optionSet == null || optionSet.has(CLI_OPTION_HELP) || !optionSet.hasOptions()){
            logger.info("See CLI usage below\n");
            optionParser.printHelpOn(System.out);
            return;
        }

        File applicationFolder = (File) optionSet.valueOf(CLI_OPTION_ORIGINAL_APP_FOLDER);
        String templateClassName = (String) optionSet.valueOf(CLI_OPTION_TEMPLATE);
        File transformedApplicationFolder = (File) optionSet.valueOf(CLI_OPTION_TRANSFORMED_APP_FOLDER);
        boolean createZip = optionSet.has(CLI_OPTION_CREATE_ZIP);

        if(createZip) {
            logger.info("-z option has been set, transformed application will be placed into a zip file");
        }

        Configuration configuration = new Configuration(transformedApplicationFolder, createZip);

        // Setting extensions log level to DEBUG
        if(optionSet.has(CLI_OPTION_VERBOSE)) {
            Set<Extension> registeredExtensions = butterflyFacade.getRegisteredExtensions();
            for(Extension extension : registeredExtensions) {
                logger.debug("Setting DEBUG log level for extension {}", extension.getClass().getName());
                verboseConfigurator.setLoggerLevel(extension.getClass().getPackage().getName(), Level.DEBUG);
            }
        }

        if(optionSet.has(CLI_OPTION_LIST_EXTENSIONS)) {
            try {
                printExtensionsList(butterflyFacade);
            } catch (Exception e) {
                logger.error("An error when listing extensions has occurred", e);
            }
            return;
        }

        try {
            butterflyFacade.transform(applicationFolder, templateClassName, configuration);
            logger.info("Application has been transformed");
        } catch (TransformationException e) {
            logger.error("A transformation error has occurred", e);
        } catch (ButterflyException e) {
            logger.error("An error has occurred", e);
        }
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

        // Transformation template option
        optionParser.accepts(CLI_OPTION_TEMPLATE, "The Java class name of the transformation template to be executed")
                .requiredUnless(CLI_OPTION_LIST_EXTENSIONS)
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("template");

        // Transformed application folder option
        optionParser.accepts(CLI_OPTION_TRANSFORMED_APP_FOLDER, "The folder location in the file system where the transformed application should be placed. It defaults to same location where original application is. Transformed application is placed under a new folder whose named is same as original folder, plus \"-transformed-yyyyMMddHHmmssSSS\" suffix")
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
        Set<Extension> registeredExtensions = butterflyFacade.getRegisteredExtensions();

        if(registeredExtensions.size() == 0) {
            logger.info("There are no registered extensions");
            return;
        }

        logger.info("See registered extensions below");

        Extension extension;
        Class<? extends TransformationTemplate> template;
        for(Object extensionObj : registeredExtensions.toArray()) {
            extension = (Extension) extensionObj;
            System.out.printf("%n- %s: %s%n", extension, extension.getDescription());
            for(Object templateObj : extension.getTemplateClasses().toArray()) {
                template = (Class<? extends TransformationTemplate>) templateObj;
                System.out.printf("\t* [%s] %s: %s%n", ExtensionTypeInitial.getFromClass(template), template.getName(), template.newInstance().getDescription());
            }
        }
    }

    private enum ExtensionTypeInitial {
        T, US, UP, V;

        public static ExtensionTypeInitial getFromClass(Class<? extends TransformationTemplate> template) {
            if(UpgradeStep.class.isAssignableFrom(template)) return US;

            // TODO
//            if(UpgradePath.class.isAssignableFrom(template)) return UP;

            if(TransformationTemplate.class.isAssignableFrom(template)) return T;

            // TODO
//            if(Validation.class.isAssignableFrom(template)) return V;

            throw new IllegalArgumentException("Class " + template.getName() + " is not recognized as an extension type");
        }
    }

}