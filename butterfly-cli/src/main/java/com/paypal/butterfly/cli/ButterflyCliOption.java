package com.paypal.butterfly.cli;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;

import static java.util.Arrays.asList;

/**
 * Butterfly CLI options and banner.
 * This is just a way to make it easier to share constants and
 * the CLI option parser with ButterflyCliApp and ButterflyCliRunner
 *
 * @author facarvalho
 */
abstract class ButterflyCliOption {

    // Possible CLI options. See method createOptionSet for details about them
    protected static final String CLI_OPTION_HELP = "h";
    protected static final String CLI_OPTION_VERBOSE = "v";
    protected static final String CLI_OPTION_DEBUG = "d";
    protected static final String CLI_OPTION_LIST_EXTENSIONS = "l";
    protected static final String CLI_OPTION_TRANSFORMED_APP_FOLDER = "o";
    protected static final String CLI_OPTION_TEMPLATE = "t";
    protected static final String CLI_OPTION_CREATE_ZIP = "z";
    protected static final String CLI_OPTION_TEMPLATE_SHORTCUT = "s";
    protected static final String CLI_OPTION_UPGRADE_VERSION = "u";
    protected static final String CLI_OPTION_RESULT_FILE = "r";

    protected static final OptionParser optionParser = new OptionParser();
    protected static OptionSet optionSet;

    static {
        // Help option
        optionParser.acceptsAll(asList(CLI_OPTION_HELP, "?"), "Show this help")
                .forHelp();

        // List extensions option
        optionParser.accepts(CLI_OPTION_LIST_EXTENSIONS, "List all registered extensions and their transformation templates");

        // Transformation template shortcut option
        optionParser.accepts(CLI_OPTION_TEMPLATE_SHORTCUT, "The shortcut number to the transformation template to be executed. If both shortcut (-s) and template class (-t) name are supplied, the shortcut will be ignored. If the chosen transformation template is an upgrade template, then the application will be upgraded all the way to the latest version possible, unless upgrade version (-u) is specified")
                .withRequiredArg()
                .ofType(Integer.class)
                .describedAs("template shortcut");

        // Transformation template option
        optionParser.accepts(CLI_OPTION_TEMPLATE, "The Java class name of the transformation template to be executed. This option has precedence over -s. If the chosen transformation template is an upgrade template, then the application will be upgraded all the way to the latest version possible, unless upgrade version (-u) is specified")
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
        optionParser.accepts(CLI_OPTION_VERBOSE, "Runs Butterfly in verbose mode, printing log messages not just in a log file, but also on the console");

        // Debug option
        optionParser.accepts(CLI_OPTION_DEBUG, "Runs Butterfly in debug mode");

        // Create Zip option
        optionParser.accepts(CLI_OPTION_CREATE_ZIP, "Outputs a zip file instead of a folder");

        // Result file option
        optionParser.accepts(CLI_OPTION_RESULT_FILE, "Creates a result file in JSON format containing details, not about the transformation itself, but about the CLI execution")
                .withRequiredArg()
                .ofType(File.class)
                .describedAs("result file");
    }

    public static void setOptionSet(String... args) {
        optionSet = optionParser.parse(args);
    }

}
