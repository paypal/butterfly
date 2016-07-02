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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    private Set<Extension> extensions = new HashSet<Extension>();

    public ExtensionRegistry() {
        Set<Class<? extends Extension>> extensionClasses = findExtensionClasses();
        registryExtensions(extensionClasses);
    }

    /**
     * Copy all entries that are a JAR file or a directory
     */
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

    private void registryExtensions(Set<Class<? extends Extension>> extensionClasses) {
        Class<? extends Extension> extensionClass;
        Extension extension;
        for(Object extensionClassObj : extensionClasses.toArray()) {
            extensionClass = (Class<? extends Extension>) extensionClassObj;
            try {
                extension = extensionClass.newInstance();
                extensions.add(extension);
            } catch (Exception e) {
                logger.error("Error when registering extension class " + extensionClass, e);
            }
        }
    }

    /**
     * Returns an immutable set of all registered extensions
     *
     * @return an immutable set of all registered extensions
     */
    public Set<Extension> getExtensions() {
        // TODO create an immutable clone of extensions, and return it here instead
        return extensions;
    }

}
