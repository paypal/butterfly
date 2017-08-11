package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import org.apache.commons.io.FilenameUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Registry of all extensions. This class is used mostly for meta-data
 * purposes, and also to make sure the TransformationTemplate classes
 * are really coming from extensions
 *
 * @author facarvalho
 */
@Component
public class ExtensionRegistry {

    private static Logger logger = LoggerFactory.getLogger(ExtensionRegistry.class);

    private List<Extension> extensions;

    private void setExtensions() {
        logger.debug("Searching for extensions");
        Set<Class<? extends Extension>> extensionClasses = findExtensionClasses();
        logger.debug("Number of extensions found: " + extensionClasses.size());

        if(extensionClasses.size() == 0) {
            extensions = Collections.emptyList();
            return;
        }

        logger.debug("Registering extensions");
        registerExtensions(extensionClasses);
        logger.debug("Extensions have been registered");
    }

    /**
     * Copy all entries that are a JAR file or a directory
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings (value="DMI_COLLECTION_OF_URLS", justification="All of type file")
    private void copyValidClasspathEntries(Collection<URL> source, Set<URL> destination) {
        String fileName;
        boolean isJarFile;
        boolean isDirectory;

        for (URL url : source) {
            if(destination.contains(url)) {
                continue;
            }

            fileName = url.getFile();
            isJarFile = FilenameUtils.isExtension(fileName, "jar");
            isDirectory = new File(fileName).isDirectory();

            if (isJarFile || isDirectory) {
                destination.add(url);
            } else if (logger.isDebugEnabled()) {
                logger.debug("Ignored classpath entry: " + fileName);
            }
        }
    }

    private Set<Class<? extends Extension>>  findExtensionClasses() {
        final Collection<URL> systemPropertyURLs = ClasspathHelper.forJavaClassPath();
        final Collection<URL> classLoaderURLs = ClasspathHelper.forClassLoader();

        Set<URL> classpathURLs = new HashSet<URL>();

        copyValidClasspathEntries(systemPropertyURLs, classpathURLs);
        copyValidClasspathEntries(classLoaderURLs, classpathURLs);

        logger.debug("Classpath URLs to be scanned: " + classpathURLs);

        Reflections reflections = new Reflections(classpathURLs, new SubTypesScanner());
        Set<Class<? extends Extension>> extensionClasses = reflections.getSubTypesOf(Extension.class);

        return extensionClasses;
    }

    private void registerExtensions(Set<Class<? extends Extension>> extensionClasses) {
        Class<? extends Extension> extensionClass;
        Extension extension;

        List<Extension> _extensions = new ArrayList<>();

        for(Object extensionClassObj : extensionClasses.toArray()) {
            extensionClass = (Class<? extends Extension>) extensionClassObj;
            try {
                extension = extensionClass.newInstance();
                _extensions.add(extension);
            } catch (Exception e) {
                logger.error("Error when registering extension class " + extensionClass, e);
            }
        }

        this.extensions = Collections.unmodifiableList(_extensions);
    }

    // TODO
    // Making it private while only one registered extension is supported
    // Make it public if there is a need to support multiple extensions registration
    /**
     * Returns an immutable set of all registered extensions
     *
     * @return an immutable set of all registered extensions
     */
    private List<Extension> getExtensions() {
        if(extensions == null) {
            setExtensions();
        }
        return extensions;
    }

    // TODO
    // Remove this method if there is a need to support multiple extensions registration
    /**
     * Get the registered extension. If no extension has been registered, return null.
     * If multiple extensions have been registered, throw an {@link IllegalStateException}
     *
     * @throws IllegalStateException if multiple extensions have been registered
     * @return the registered extension
     */
    public Extension getExtension() {
        int numberOfExtensions = getExtensions().size();
        if (numberOfExtensions > 1) {
            throw new IllegalStateException("More than one Butterfly extension have been registered");
        } else if (numberOfExtensions == 0) {
            return null;
        }
        return (Extension) getExtensions().toArray()[0];
    }

}
