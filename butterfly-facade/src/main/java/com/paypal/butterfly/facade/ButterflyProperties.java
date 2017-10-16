package com.paypal.butterfly.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class obtains Butterfly properties via its
 * Maven pom file and an intermediate properties file
 * (see properties-maven-plugin usage)
 *
 * @author facarvalho
 */
public class ButterflyProperties {

    private static Properties properties;

    private static Logger logger = LoggerFactory.getLogger(ButterflyProperties.class);

    public static Object get(String propertyName) {
        if (properties != null) {
            return properties.get(propertyName);
        }
        throw new IllegalStateException("Properties could not be loaded");
    }

    public static String getString(String propertyName) {
        if (properties != null) {
            return (String) properties.get(propertyName);
        }
        throw new IllegalStateException("Properties could not be loaded");
    }

    static {
        InputStream fileInputStream = null;
        try {
            fileInputStream = ButterflyProperties.class.getClassLoader().getResourceAsStream("butterfly.properties");
            properties = new Properties();
            if (fileInputStream != null) {
                properties.load(fileInputStream);
            } else {
                logger.warn("File butterfly.properties could not be found in butterfly-cli, assuming Butterfly is being run in development environment");
            }
        } catch (Exception e) {
            logger.error("Exception thrown when obtaining Butterfly version", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    logger.error("Exception thrown when obtaining Butterfly version", e);
                }
            }
        }
    }

}
