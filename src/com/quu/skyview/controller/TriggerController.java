package com.quu.skyview.controller;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.quu.skyview.model.Trigger;
import com.quu.skyview.service.ITriggerService;
import com.quu.util.Util;


@RequestScoped  //THESE ARE NOT NEEDED NOW
@Path("/trigger")
public class TriggerController {
	
	@Inject
    private ITriggerService triggerService;
		
	/*
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	//public Response add(Trigger trigger)
	public Response send(String json)
	{
		int status = triggerService.sendCampaigns(json);
		
		if(status == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	*/
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response process(Trigger trigger)
	{
		System.out.println("Received trigger: " + trigger.getEventId());
		
		int status = triggerService.process(trigger);
		
		if(status == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
}
