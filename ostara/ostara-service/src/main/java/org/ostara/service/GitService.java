package org.ostara.service;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/git")
public interface GitService {

	@POST
	@Path(value = "authenticate")
	public Object authenticate(@QueryParam("username") String username);
	
	@GET
	@Path(value = "{org}/{repo}/branches")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object branches(@PathParam("org") String organization, @PathParam("repo") String repository, @QueryParam("page") int page, @QueryParam("per_page") int perPage);

	@GET
	@Path(value = "{org}/{repo}/contents")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object contents(@PathParam(value="org") String organization, @PathParam(value="repo") String repository, 
			@QueryParam(value="path") String path, @QueryParam(value="branch") String branch);
}