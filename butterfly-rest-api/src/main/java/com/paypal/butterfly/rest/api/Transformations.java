package com.paypal.butterfly.rest.api;

import com.paypal.butterfly.rest.model.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import java.util.Date;
import java.util.List;

/**
 * Butterfly Transformations JAX-RS sub-resource Service Endpoint Interface
 *
 * @author facarvalho
 */
@Tag(name = "Transformations")
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
    TransformationState postTransformation(TransformationRequest transformationRequest);

    /**
     * Get basic information about a transformation
     */
    @GET
    @Path("/{id}")
    Transformation getTransformationById(@PathParam("id") String id);

    /**
     * Get the state of a transformation
     */
    @GET
    @Path("/{id}/state")
    TransformationState getTransformationStateById(@PathParam("id") String id);

    /**
     * Get details about a transformation
     */
    @GET
    @Path("/{id}/details")
    TransformationDetails getTransformationDetailsById(@PathParam("id") String id);

    /**
     * Get the metrics of a transformation
     */
    @GET
    @Path("/{id}/metrics")
    TransformationMetrics getTransformationMetricsById(@PathParam("id") String id);

}
