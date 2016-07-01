package com.paypal.butterfly.cli;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;

import static java.util.Arrays.asList;

@SpringBootApplication
public class ButterflyCliApp {

    public static void main(String... arguments) throws IOException {
        SpringApplication.run(ButterflyCliApp.class, arguments);

        OptionParser optionParser = new OptionParser();
        optionParser.accepts("f", "The location in the file system where the application to be transformed is").withRequiredArg().ofType(File.class).describedAs("folder");
        optionParser.accepts("t", "The id of the transformation template to be executed").withRequiredArg().ofType(String.class).describedAs("template");
        optionParser.acceptsAll(asList("h", "?"), "Show this help").forHelp();
        OptionSet optionSet = optionParser.parse(arguments);

        SpringApplication.run(ButterflyCliApp.class, arguments);

        if(optionSet.has("h") || !optionSet.hasOptions()) {
            optionParser.printHelpOn(System.out);
            return;
        }
    }

}