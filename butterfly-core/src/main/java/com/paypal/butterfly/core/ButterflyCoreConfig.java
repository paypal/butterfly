package com.paypal.butterfly.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot configuration class to automatically
 * register Butterfly Core Spring beans
 *
 * @author facarvalho
 */
@Configuration
@Import({ExtensionRegistry.class, ButteflyFacadeImpl.class, TransformationEngine.class})
public class ButterflyCoreConfig {
}
