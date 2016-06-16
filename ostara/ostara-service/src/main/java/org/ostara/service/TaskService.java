package org.ostara.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface TaskService {

	@POST
	@Path("/meta/task/add")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.TEXT_PLAIN })
	public abstract String addTaskMeta(String jsonstr);

	@GET
	@Path("/echo/{message}")
	@Produces(MediaType.TEXT_PLAIN)
	public abstract String echo(@PathParam("message") String message);

	@GET
	@Path("/query")
	@Produces({ MediaType.APPLICATION_JSON })
	public abstract Object query(@QueryParam("jobId") String jobId);

	@GET
	@Path("/queryall")
	@Produces({ MediaType.TEXT_PLAIN })
	public abstract String queryAll();

	@GET
	@Path("/meta/allcmd")
	@Produces({ MediaType.APPLICATION_JSON })
	public abstract Object queryAllCmd();

	@GET
	@Path("/meta/alltasks")
	@Produces({ MediaType.APPLICATION_JSON })
	public abstract Object queryTaskMeta();

	@GET
	@Path("/task")
	@Produces({ MediaType.APPLICATION_JSON })
	public abstract Object submitTask(@QueryParam("taskName") String taskName);

	@GET
	@Path("/upg")
	@Produces({ MediaType.APPLICATION_JSON })
	public abstract Object upgrade(@QueryParam("gitURL")
	   String gitURL, @QueryParam("gitBranch")
	   String gitBranch, @QueryParam("parentPom")
	   String parentPom, @QueryParam("upgradeVersion")
	   String upgradeVersion);

	@GET
	@Path("/report")
	@Produces({ MediaType.APPLICATION_JSON })
	public abstract Object report();
	
}