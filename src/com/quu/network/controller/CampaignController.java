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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quu.network.model.Campaign;
import com.quu.network.service.ICampaignService;


@RequestScoped
@Path("/campaign")
public class CampaignController {
	
	@Inject
    private ICampaignService campaignService;
		
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() 
	{
		List<Campaign> list = campaignService.getAll();
		
		if(list != null && !list.isEmpty())
			return Response.status(Response.Status.OK).entity(list).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@GET
	@Path("/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) 
	{
		Campaign entity = campaignService.get(id);
		
		if(entity != null)
			return Response.status(Response.Status.OK).entity(entity).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(Campaign campaign)
	{
		int id = campaignService.add(campaign);
		
		return Response.status(Response.Status.OK).entity("{\"ID\":"+id+"}").build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(Campaign campaign)
	{
		int rowUpdated = campaignService.update(campaign);
		
		if(rowUpdated > 0)
			return Response.status(Response.Status.OK).entity("{\"ID\":"+campaign.getId()+"}").build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@DELETE
	@Path("/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) 
	{
		int count = campaignService.delete(id);
		
		if(count > 0)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
}
