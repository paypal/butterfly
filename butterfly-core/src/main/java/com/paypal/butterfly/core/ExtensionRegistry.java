package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import org.springframework.stereotype.Component;

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

    private Set<Extension> extensions = new HashSet<Extension>();

    public ExtensionRegistry() {
        // TODO
        // Scan the class path looking for extension class, and register them
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
