package com.paypal.butterfly.cli;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.butterfly.cli.logging.LogFileDefiner;
import com.paypal.butterfly.facade.ButterflyFacade;

import joptsimple.OptionException;

/**
 * Butterfly CLI Spring Boot entry point
 *
 * @author facarvalho
 */
@SpringBootApplication
public class ButterflyCliApp extends ButterflyCliOption {

    private static File butterflyHome;
    private static String banner;

    private static Logger logger;

    @SuppressWarnings("PMD.DoNotCallSystemExit")
    public static void main(String... arguments) throws IOException {
        int exitStatus = run(arguments).getExitStatus();
        System.exit(exitStatus);
    }

    public static ButterflyCliRun run(String... arguments) throws IOException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ButterflyCliApp.class, arguments);
        ButterflyFacade butterflyFacade = applicationContext.getBean(ButterflyFacade.class);

        setButterflyHome();
        setEnvironment(arguments, butterflyFacade);

        logger = LoggerFactory.getLogger(ButterflyCliApp.class);

        setBanner(butterflyFacade);

        ButterflyCliRunner butterflyCliRunner = applicationContext.getBean(ButterflyCliRunner.class);
        ButterflyCliRun run = butterflyCliRunner.run();
        run.setInputArguments(arguments);

        if (optionSet != null && optionSet.has(CLI_OPTION_RESULT_FILE)) {
            writeResultFile(run);
        }

        return run;
    }

    private static void setButterflyHome() {
        String butterflyHomePath = System.getenv("BUTTERFLY_HOME");
        if (butterflyHomePath == null) {
            butterflyHomePath = System.getProperty("user.dir");
        }
        butterflyHome = new File(butterflyHomePath);
    }

    @SuppressWarnings("PMD.DoNotCallSystemExit")
    private static void setEnvironment(String[] arguments, ButterflyFacade butterflyFacade) {
        if(arguments.length != 0){
            try {
                setOptionSet(arguments);
                File applicationFolder = getApplicationFolder();
                boolean debug = optionSet.has(CLI_OPTION_DEBUG);
                LogFileDefiner.setLogFileName(applicationFolder, debug);
            } catch (OptionException e) {
                Logger logger = LoggerFactory.getLogger(ButterflyCliApp.class);
                setBanner(butterflyFacade);
                logger.info(getBanner());
                logger.error(e.getMessage());
                System.exit(1);
            }
        }
    }

    /*
     * Returns the application folder only if it has been passed
     * as an input argument that is really an existent folder.
     * Otherwise, returns null.
     */
    private static File getApplicationFolder() {
        List<?> nonOptionArguments = optionSet.nonOptionArguments();
        if (nonOptionArguments == null || nonOptionArguments.size() == 0 || StringUtils.isEmpty(nonOptionArguments.get(0))) {
            return null;
        }
        File applicationFolder = new File((String) nonOptionArguments.get(0));
        if (!applicationFolder.exists()) {
            return null;
        }

        return applicationFolder;
    }

    private static void setBanner(ButterflyFacade butterflyFacade) {

        // Ideally the version should be gotten from the façade Spring bean.
        // However, it is not available this early, so we are getting it directly
        // from the CLI artifact, assuming that the CLI jar will always bring together
        // the exact same version of butterfly-core, which is the component to officially
        // define Butterfly version
        banner = String.format("Butterfly application transformation tool (version %s)", butterflyFacade.getButterflyVersion());
    }

    public static File getButterflyHome() {
        return butterflyHome;
    }

    // This method's visibility is intentionally being set to package
    @SuppressWarnings("PMD.DefaultPackage")
    static String getBanner() {
        return banner;
    }

    private static void writeResultFile(ButterflyCliRun run) {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        String runJsonString = gson.toJson(run);
        File resultFile = (File) optionSet.valueOf(CLI_OPTION_RESULT_FILE);
        try {
            FileUtils.writeStringToFile(resultFile, runJsonString, Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Error when writing CLI result file", e);
        }
    }

}