package com.paypal.butterfly.cli;

import com.paypal.butterfly.extensions.api.Extension;
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

    private static final String BANNER = "\nButterfly application transformation tool (version %s). %s.\n\n";
    private static final String VERSION = "1.0.0-SNAPSHOT";

    private static Logger logger = LoggerFactory.getLogger(ButterflyCliApp.class);

    public static void main(String... arguments) throws IOException {
        OptionParser optionParser = createOptionSet();
        OptionSet optionSet = optionParser.parse(arguments);

        ConfigurableApplicationContext applicationContext = SpringApplication.run(ButterflyCliApp.class, arguments);

        if(optionSet.has("h") || !optionSet.hasOptions()) {
            printBanner("See CLI usage below");
            optionParser.printHelpOn(System.out);
            return;
        }

        if(optionSet.has("v")) {
            VerboseConfigurator verboseConfigurator = applicationContext.getBean(VerboseConfigurator.class);
            verboseConfigurator.verboseMode(true);
            logger.debug("Verbose mode is ON");
        }

        File applicationFolder = (File) optionSet.valueOf("f");
        String templateClassName = (String) optionSet.valueOf("t");

        ButterflyFacade butterflyFacade = applicationContext.getBean(ButterflyFacade.class);

        if(optionSet.has("l")) {
            printBanner("See registered extensions below");

            Set<Extension> registeredExtensions = butterflyFacade.getRegisteredExtensions();
            // TODO print properly all extensions and their templates on system out
            System.out.println(registeredExtensions);

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
        optionParser.acceptsAll(asList("h", "?"), "Show this help")
                .forHelp();

        // List extensions option
        optionParser.accepts("l", "List all registered extensions");

        // Application folder option
        optionParser.accepts("f", "The folder location in the file system where the application to be transformed is")
                .requiredUnless("l")
                .withRequiredArg()
                .ofType(File.class)
                .describedAs("folder");

        // Transformation template option
        optionParser.accepts("t", "The Java class name of the transformation template to be executed")
                .requiredUnless("l")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("template");

        // Verbose option
        optionParser.accepts("v", "Runs Butterfly in verbose mode");

        return optionParser;
    }

    private static void printBanner(String subMessage) {
        System.out.printf(BANNER, VERSION, subMessage);
    }

}