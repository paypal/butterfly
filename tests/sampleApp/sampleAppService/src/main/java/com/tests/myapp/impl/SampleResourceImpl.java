package com.tests.myapp.impl;

import com.tests.myapp.api.SampleResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Component
@Scope("request")
@Path("/sampleresource")
public class SampleResourceImpl implements SampleResource {

    public Hello hello = new Hello();

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    @Path("/hello")
    @Override
    public String sayHello() { 	
        return hello.sayHello();
    }

}
