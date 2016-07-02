package com.paypal.butterfly.cli;

import com.paypal.butterfly.extensions.api.Extension;
import com.paypal.butterfly.extensions.api.TransformationTemplate;
import com.paypal.butterfly.facade.ButterflyFacade;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Butterfly Command Line Interface application
 *
 * @author facarvalho
 */
@SpringBootApplication
public class ButterflyCliApp {

    private static final String VERSION = "1.0.0-SNAPSHOT";
    private static final String BANNER = "Butterfly application transformation tool (version " + VERSION + ")";

    private static final String CLI_OPTION_HELP = "h";
    private static final String CLI_OPTION_VERBOSE = "v";
    private static final String CLI_OPTION_LIST_EXTENSIONS = "l";
    private static final String CLI_OPTION_ORIGINAL_APP_FOLDER = "i";
    private static final String CLI_OPTION_TRANSFORMED_APP_FOLDER = "o";
    private static final String CLI_OPTION_TEMPLATE = "t";

    private static Logger logger = LoggerFactory.getLogger(ButterflyCliApp.class);

    public static void main(String... arguments) throws IOException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ButterflyCliApp.class, arguments);

        logger.info(BANNER);

        OptionParser optionParser = createOptionSet();
        OptionSet optionSet = optionParser.parse(arguments);

        if(optionSet.has(CLI_OPTION_VERBOSE)) {
            VerboseConfigurator verboseConfigurator = applicationContext.getBean(VerboseConfigurator.class);
            verboseConfigurator.verboseMode(true);
            logger.debug("Verbose mode is ON");
        }

        if(optionSet.has(CLI_OPTION_HELP) || !optionSet.hasOptions()) {
            logger.info("See CLI usage below\n");
            optionParser.printHelpOn(System.out);
            return;
        }

        File applicationFolder = (File) optionSet.valueOf("i");
        String templateClassName = (String) optionSet.valueOf("t");

        ButterflyFacade butterflyFacade = applicationContext.getBean(ButterflyFacade.class);

        if(optionSet.has(CLI_OPTION_LIST_EXTENSIONS)) {
            logger.info("See registered extensions below");
            printExtensionsList(butterflyFacade);
            return;
        }

        try {
            butterflyFacade.transform(applicationFolder, templateClassName);
            logger.info("Application has been transformed");
        } catch (Exception e) {
            logger.error("Transformation error has occurred", e);
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
        optionParser.accepts(CLI_OPTION_TRANSFORMED_APP_FOLDER, "The folder location in the file system where the transformed application should be placed. It defaults to same location where original application is. Transformed application is placed under a new folder whose named is same as original folder, plus \"-transformed-<timestamp>\" suffix")
                .withRequiredArg()
                .ofType(File.class)
                .describedAs("output");

        // Verbose option
        optionParser.accepts(CLI_OPTION_VERBOSE, "Runs Butterfly in verbose mode");

        return optionParser;
    }

    private static void printExtensionsList(ButterflyFacade butterflyFacade) {
        Set<Extension> registeredExtensions = butterflyFacade.getRegisteredExtensions();
        Extension extension;
        Class<? extends TransformationTemplate> template;
        for(Object extensionObj : registeredExtensions.toArray()) {
            extension = (Extension) extensionObj;
            System.out.printf("\n- %s:\n", extension);
            for(Object templateObj : extension.getTemplateClasses().toArray()) {
                template = (Class<? extends TransformationTemplate>) templateObj;
                System.out.printf("\t[T] %s\n", template.getName());
            }
        }
    }

}