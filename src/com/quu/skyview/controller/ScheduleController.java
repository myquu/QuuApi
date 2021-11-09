package com.quu.skyview.controller;

import java.util.List;
import java.util.Map;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quu.skyview.model.Advertisement;
import com.quu.skyview.model.Break;
import com.quu.skyview.model.Event;
import com.quu.skyview.model.Schedule;
import com.quu.skyview.service.IScheduleService;


@RequestScoped
@Path("/schedule")
public class ScheduleController {
	
	@Inject
    private IScheduleService scheduleService;
	
	
	@GET
	@Path("/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int station_id) 
	{
		Schedule entity = scheduleService.get(station_id);
		
		if(entity != null)
			return Response.status(Response.Status.OK).entity(entity).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Schedule schedule)
	{
		int ret = scheduleService.add(schedule);
		
		if(ret > -1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(Schedule schedule)
	{
		int ret = scheduleService.update(schedule);
		
		if(ret > -1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	//Delete will delete only a part of the schedule, not entirely.
	@DELETE
	@Path("/{StationID: \\d+}/{EventID: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("StationID") int station_id, @PathParam("EventID") int eventId)
	{
		int ret = scheduleService.deleteEventCascade(station_id, eventId);
		
		if(ret > -1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
		
}
