package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;

/**
 * Registry of all extensions. This class is used mostly for meta-data
 * purposes, and also to make sure the TransformationTemplate classes
 * are really coming from extensions
 *
 * @author facarvalho
 */
@Component
class ExtensionRegistry {

    private static Logger logger = LoggerFactory.getLogger(ExtensionRegistry.class);

    private List<Extension<?>> extensions;

    private void initExtensions() {
        logger.debug("Searching for extensions");
        Set<Class<? extends Extension<?>>> extensionClasses = findExtensionClasses();
        logger.debug("Number of extensions found: {}", extensionClasses.size());

        if (extensionClasses.isEmpty()) {
            extensions = Collections.emptyList();
            return;
        }

        logger.debug("Registering extensions");
        registerExtensions(extensionClasses);
        logger.debug("Extensions have been registered");
    }

    private Set<Class<? extends Extension<?>>> findExtensionClasses() {
        // Assumption here is that context loader is already a spring-boot ClassLoader and therefore is capable of
        // loading classes from nested jars. If the assumption is not true, we can easily create such a ClassLoader
        // with SpringBootClassLoaderFactory currently used only in tests.
        ClassGraph classGraph = new ClassGraph()
                .enableClassInfo()
                .removeTemporaryFilesAfterScan();

        try (ScanResult scanResult = classGraph.scan()) {
            return scanResult.getSubclasses(Extension.class.getName())
                    .stream()
                    .map(ClassInfo::loadClass)
                    .map(clazz -> asExtensionClass(clazz))
                    .collect(toCollection(() -> new TreeSet<>(comparing(Class::getName))));
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Extension<?>> asExtensionClass(Class<?> clazz) {
        return (Class<? extends Extension<?>>) clazz;
    }

    private void registerExtensions(Set<Class<? extends Extension<?>>> extensionClasses) {
        List<Extension<?>> newExtensions = new ArrayList<>();

        for (Class<? extends Extension<?>> extensionClass : extensionClasses) {
            try {
                Extension<?> extension = extensionClass.getConstructor().newInstance();
                newExtensions.add(extension);
            } catch (Exception e) {
                logger.error("Cannot register extension class {}", extensionClass, e);
            }
        }

        this.extensions = newExtensions;
    }

    /**
     * Returns an immutable list with all registered extensions
     *
     * @return an immutable list with all registered extensions
     */
    @SuppressWarnings("rawtypes")
    List<Extension> getExtensions() {
        if (extensions == null) {
            initExtensions();
        }
        return Collections.unmodifiableList(extensions);
    }

}
