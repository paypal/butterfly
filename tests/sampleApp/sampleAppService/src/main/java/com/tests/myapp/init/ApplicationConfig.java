package com.tests.myapp.init;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * This is a JAX-RS compliant way to map your JAX-RS root context for your
 * application. The resources will be mapped to the ApplicationPath defined 
 * in this annotation and the @PATH annotation will be appended to it.
 */
@ApplicationPath("/v1/sampleapp/")
public class ApplicationConfig extends Application {
}
