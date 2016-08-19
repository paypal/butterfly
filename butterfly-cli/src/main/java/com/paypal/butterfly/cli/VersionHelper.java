package com.paypal.butterfly.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class obtains Butterfly version via its
 * Maven pom file and an intermediate properties file
 * (see properties-maven-plugin usage), and makes it
 * accessible to the CLI tool to log it
 *
 * @author facarvalho
 */
public class VersionHelper {

    private static final String UNKNOWN_VERSION = "UNKNOWN_VERSION";

    private static Logger logger = LoggerFactory.getLogger(VersionHelper.class);

    public static String getButterflyVersion() {
        return getButterflyVersionFromProperties();
    }

    private static String getButterflyVersionFromProperties() {
        String butterflyVersion = UNKNOWN_VERSION;

        InputStream fileInputStream = null;
        try {
            fileInputStream = VersionHelper.class.getClassLoader().getResourceAsStream("butterfly.properties");
            Properties properties = new Properties();
            properties.load(fileInputStream);
            butterflyVersion = properties.getProperty("butterfly.version");
        } catch (Exception e) {
            logger.error("Error happened when obtaining Butterfly version. See debug log statements for more details.");
            logger.error("Exception thrown when obtaining Butterfly version", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    logger.error("Error happened when obtaining Butterfly version. See debug log statements for more details.");
                    logger.error("Exception thrown when obtaining Butterfly version", e);
                }
            }
        }

        return butterflyVersion;
    }

}
