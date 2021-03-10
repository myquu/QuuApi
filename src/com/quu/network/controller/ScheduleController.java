package com.quu.network.controller;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.quu.network.model.Schedule;
import com.quu.network.service.IScheduleService;


@RequestScoped
@Path("/schedule")
public class ScheduleController {
	
	@Inject
    private IScheduleService scheduleService;
		
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() 
	{
		List<Schedule> list = scheduleService.getAll();
		
		if(list != null && !list.isEmpty())
			return Response.status(Response.Status.OK).entity(list).build();
		else
			//return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"Message\": \"PK violated\"}").build();
			//The below can be done but it will not output the message
			//throw new WebApplicationException("PK violated", Response.Status.INTERNAL_SERVER_ERROR);
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@GET
	@Path("/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) 
	{
		Schedule entity = scheduleService.get(id);
		
		if(entity != null)
			return Response.status(Response.Status.OK).entity(entity).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Schedule schedule)
	{
		int id = scheduleService.add(schedule);
		
		return Response.status(Response.Status.OK).entity(id).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(Schedule schedule)
	{
		int id = scheduleService.update(schedule);
		
		return Response.status(Response.Status.OK).entity(id).build();
	}
	
	@DELETE
	@Path("/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) 
	{
		boolean status = scheduleService.delete(id);
		
		if(status)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
}
