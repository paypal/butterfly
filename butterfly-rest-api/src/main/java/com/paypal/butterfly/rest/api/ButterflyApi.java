package com.paypal.butterfly.rest.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.Path;

/**
 * Butterfly API JAX-RS root resource
 *
 * @author facarvalho
 */
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Butterfly",
                version = "1",
                description = "Butterfly API",
                license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT"),
                contact = @Contact(name = "Butterfly", url = "https://github.com/paypal/butterfly")
        ),
        tags = {
                @Tag(name = "Transformations"),
                @Tag(name = "Info")
        },
        servers = {
                @Server(
                        description = "Dev",
                        url = "http://localhost:3000"
                )
        }
)
@Path("/v1")
public interface ButterflyApi {

    @Path("/info")
    Info info();

    @Path("/transformations")
    Transformations transformations();

}
