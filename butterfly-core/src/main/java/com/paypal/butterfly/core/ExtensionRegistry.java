package com.paypal.butterfly.core;

import com.paypal.butterfly.extensions.api.Extension;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Controls all extensions
 *
 * @author facarvalho
 */
@Component
public class ExtensionRegistry {

    private Set<Extension> extensions;

}
