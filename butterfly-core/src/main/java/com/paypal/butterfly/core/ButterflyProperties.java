package com.paypal.butterfly.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class obtains Butterfly properties via its
 * Maven pom file and an intermediate properties file
 * (see properties-maven-plugin usage)
 *
 * @author facarvalho
 */
class ButterflyProperties {

    private static Properties properties;

    private static Logger logger = LoggerFactory.getLogger(ButterflyProperties.class);

    /**
     * Returns a property value as Object, given its name, or null,
     * if there is no property with such name
     *
     * @param propertyName the name of the property
     * @return a property value, given its name, or null,
     *         if there is no property with such name
     * @throws IllegalArgumentException if the specified property name is null or blank
     */
    static Object get(String propertyName) {
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("Property name cannot be null nor blank");
        }
        if (properties != null) {
            return properties.get(propertyName);
        }
        throw new IllegalStateException("Properties could not be loaded");
    }

    /**
     * Retrieves a property value as String, given its name.
     * If the property value is not a String, its {@link #toString()}
     * value will be returned instead
     *
     * @param propertyName the name of the property
     * @return a property value, given its name, or null,
     *         if there is no property with such name
     * @throws IllegalArgumentException if the specified property name is null or blank
     */
    static String getString(String propertyName) {
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("Property name cannot be null nor blank");
        }
        if (properties != null) {
            Object value = properties.get(propertyName);
            if (value == null) {
                return null;
            }
            return value.toString();
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
            logger.error("An exception happened when obtaining Butterfly version", e);
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
