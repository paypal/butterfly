package com.tests.myapp.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;

@Path("/sampleresource")
public interface SampleResource {

    @GET
    @Produces({ MediaType.TEXT_PLAIN })
    @Path("/hello")
    String sayHello();

}
