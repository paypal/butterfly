package com.paypal.butterfly.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.paypal.butterfly.api.ButterflyFacade;
import com.paypal.butterfly.cli.logging.LogFileDefinition;
import joptsimple.OptionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.function.Failable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Butterfly CLI Spring Boot entry point
 *
 * @author facarvalho
 */
@SpringBootApplication(scanBasePackages = "com.paypal.butterfly")
class ButterflyCliApp extends ButterflyCliOption {

    private File butterflyHome;
    private String banner;

    private Logger logger;

    @SuppressWarnings("PMD.DoNotCallSystemExit")
    public static void main(String... arguments) {
        ButterflyCliApp butterflyCliApp = new ButterflyCliApp();
        int exitStatus = butterflyCliApp.run(arguments).getExitStatus();
        System.exit(exitStatus);
    }

    private Class<?> getButterflyCliAppClass() {
        final String className = System.getProperty("butterfly.config", "com.paypal.butterfly.cli.ButterflyCliApp");
        return Failable.call(() -> Class.forName(className, true, ClassUtils.getDefaultClassLoader()));
    }

    ButterflyCliRun run(String... arguments) {
        setButterflyHome();

        LogFileDefinition.getInstance().setButterflyHome(butterflyHome);

        setEnvironment(arguments);

        logger = LoggerFactory.getLogger(ButterflyCliApp.class);

        ConfigurableApplicationContext applicationContext = SpringApplication.run(getButterflyCliAppClass(), arguments);
        ButterflyFacade butterflyFacade = applicationContext.getBean(ButterflyFacade.class);

        setBanner(butterflyFacade);

        ButterflyCliRunner butterflyCliRunner = applicationContext.getBean(ButterflyCliRunner.class);
        ButterflyCliRun run = butterflyCliRunner.run(butterflyHome, banner);
        run.setInputArguments(arguments);

        if (optionSet != null && optionSet.has(CLI_OPTION_RESULT_FILE)) {
            writeResultFile(run);
        }

        return run;
    }

    private void setButterflyHome() {
        String butterflyHomePath = System.getenv("BUTTERFLY_HOME");
        if (butterflyHomePath == null) {
            butterflyHomePath = System.getProperty("user.dir");
        }
        butterflyHome = new File(butterflyHomePath);
    }

    @SuppressWarnings("PMD.DoNotCallSystemExit")
    private void setEnvironment(String[] arguments) {
        if(arguments.length != 0){
            try {
                setOptionSet(arguments);
                File applicationFolder = getApplicationFolder();
                boolean debug = optionSet.has(CLI_OPTION_DEBUG);

                LogFileDefinition.getInstance().setLogFileName(applicationFolder, debug);
            } catch (OptionException e) {
                Logger logger = LoggerFactory.getLogger(ButterflyCliApp.class);
                logger.info("Butterfly application transformation tool");
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
    private File getApplicationFolder() {
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

    private void setBanner(ButterflyFacade butterflyFacade) {

        // Ideally the version should be gotten from the façade Spring bean.
        // However, it is not available this early, so we are getting it directly
        // from the CLI artifact, assuming that the CLI jar will always bring together
        // the exact same version of butterfly-core, which is the component to officially
        // define Butterfly version
        banner = String.format("Butterfly application transformation tool (version %s)", butterflyFacade.getButterflyVersion());
    }

    private void writeResultFile(ButterflyCliRun run) {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setPrettyPrinting().registerTypeAdapter(File.class, new TypeAdapter<File>() {
            @Override
            public void write(JsonWriter jsonWriter, File file) throws IOException {
                String fileAbsolutePath = (file == null ? null : file.getAbsolutePath());
                jsonWriter.value(fileAbsolutePath);
            }
            @Override
            public File read(JsonReader jsonReader) {
                throw new UnsupportedOperationException("There is no support for deserializing transformation result objects at the moment");
            }
        });
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
