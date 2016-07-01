package com.paypal.butterfly.cli;

import com.paypal.butterfly.facade.ButterflyFacade;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;

import static java.util.Arrays.asList;

/**
 * Butterfly Command Line Interface application
 *
 * @author facarvalho
 */
@SpringBootApplication
public class ButterflyCliApp {

    public static void main(String... arguments) throws IOException {
        OptionParser optionParser = createOptionSet();
        OptionSet optionSet = optionParser.parse(arguments);

        ConfigurableApplicationContext applicationContext = SpringApplication.run(ButterflyCliApp.class, arguments);

        if(optionSet.has("h") || !optionSet.hasOptions()) {
            optionParser.printHelpOn(System.out);
            return;
        }

        File applicationFolder = (File) optionSet.valueOf("f");
        String templateName = (String) optionSet.valueOf("t");

        ButterflyFacade butterflyFacade = applicationContext.getBean(ButterflyFacade.class);
        butterflyFacade.transform(applicationFolder, templateName);

        System.out.println("Application has been transformed");
    }

    private static OptionParser createOptionSet() {
        OptionParser optionParser = new OptionParser();

        // Help option
        optionParser.acceptsAll(asList("h", "?"), "Show this help")
                .forHelp();

        // Application folder option
        optionParser.accepts("f", "The folder location in the file system where the application to be transformed is")
                .withRequiredArg()
                .ofType(File.class)
                .describedAs("folder")
                .required();

        // Transformation template option
        optionParser.accepts("t", "The id of the transformation template to be executed")
                .requiredUnless("h")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs("template")
                .required();

        return optionParser;
    }

}