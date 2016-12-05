package com.tests.myapp.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.tests.myapp.api.SampleResource;

/**
 * This resource shows how to use JAX-RS injection and how to use
 * a spring bean as a JAX-RS resource class.
 *
 * Notice that the scope for this bean is request which means that a new
 * instance of this class will be created per request.
 * 
 */

@Component
@Scope("request")
@Path("/sampleresource")
public class SampleResourceImpl implements SampleResource {
	public SampleResourceImpl() {
	};

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    @Path("/hello")
    @Override
    public String sayHello() { 	
        return "Hello, World '" + Math.round(Math.random() * 1000) + "'!";
    }

}
