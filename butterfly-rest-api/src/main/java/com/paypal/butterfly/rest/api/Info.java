package com.paypal.butterfly.rest.api;

import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Butterfly Info JAX-RS sub-resource Service Endpoint Interface
 *
 * @author facarvalho
 */
@Tag(name = "Info")
@Produces("application/json")
@Consumes("application/json")
public interface Info {

    /**
     * Get Butterfly version
     */
    @GET
    @Path("version")
    String getVersion();

}
