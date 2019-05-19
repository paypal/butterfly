package com.paypal.butterfly.rest.api;

import com.paypal.butterfly.rest.model.*;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import java.util.Date;
import java.util.List;

@OpenAPIDefinition(
        info = @Info(
                title = "Butterfly",
                version = "0.1",
                description = "Butterfly API",
                license = @License(name = "MIT License", url = "https://opensource.org/licenses/MIT"),
                contact = @Contact(name = "Butterfly", url = "https://github.com/paypal/butterfly")
        ),
        tags = {
                @Tag(name = "Transformations")
        },
        servers = {
                @Server(
                        description = "Dev",
                        url = "http://localhost:3000/v1"
                )
        }
)
@Path("/transformations")
@Produces("application/json")
@Consumes("application/json")
public interface Transformations {

    /**
     * Get a list of transformations
     */
    @GET
    List<Transformation> getTransformations(@QueryParam("offset") @DefaultValue("0") int offset,
                                            @QueryParam("limit") @DefaultValue("20") @Min(5) @Max(100) int limit,
                                            @QueryParam("user") String user,
                                            @QueryParam("date") Date date,
                                            @QueryParam("dateStart") Date dateStart,
                                            @QueryParam("dateEnd") Date dateEnd,
                                            @QueryParam("state") TransformationState.State state,
                                            @QueryParam("extensionName") String extensionName,
                                            @QueryParam("templateClassName") String templateClassName,
                                            @QueryParam("templateType") TransformationTemplateType templateType,
                                            @QueryParam("versionFrom") String versionFrom,
                                            @QueryParam("versionTo") String versionTo);

    /**
     * Request a transformation
     */
    @POST
    TransformationState postTransformations(TransformationRequest entity);

    /**
     * Get basic information about a transformation
     */
    @GET
    @Path("/{id}")
    Transformation getTransformationsById(@PathParam("id") String id);

    /**
     * Get the state of a transformation
     */
    @GET
    @Path("/{id}/state")
    TransformationState getTransformationsStateById(@PathParam("id") String id);

    /**
     * Get details about a transformation
     */
    @GET
    @Path("/{id}/details")
    TransformationDetails getTransformationsDetailsById(@PathParam("id") String id);

    /**
     * Get the metrics of a transformation
     */
    @GET
    @Path("/{id}/metrics")
    TransformationMetrics getTransformationsMetricsById(@PathParam("id") String id);

}
