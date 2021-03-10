package com.quu.vcreative.controller;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignStation;
import com.quu.vcreative.service.ICampaignService;


@RequestScoped
@Path("/campaign")
public class CampaignController {
	
	@Inject
    private ICampaignService campaignService;
		
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(CampaignIn campaign)
	{
		int id = campaignService.add(campaign);
		
		return Response.status(Response.Status.OK).entity("{\"ID\":"+id+"}").build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(CampaignIn campaign)
	{
		int count = campaignService.update(campaign);
		
		if(count > 0)
			return Response.status(Response.Status.OK).entity("{\"ID\":"+campaign.getId()+"}").build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Path("/assign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assignStationsCarts(CampaignStation campaignStation)
	{
		int id = campaignService.assignStationsCarts(campaignStation);
		
		return Response.status(Response.Status.OK).entity("{\"ID\":"+id+"}").build();
	}
			
	@DELETE
	@Path("/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deactivate(@PathParam("id") int id) 
	{
		int count = campaignService.deactivate(id);
		
		if(count > 0)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
}
