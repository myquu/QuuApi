package com.quu.watermark.controller;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quu.watermark.model.CampaignIn;
import com.quu.watermark.model.CampaignOut;
import com.quu.watermark.service.ICampaignService;


@RequestScoped
@Path("/campaign")
public class CampaignController {
	
	@Inject
    private ICampaignService campaignService;
		
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(CampaignIn campaignIn)
	{
		CampaignOut campaignOut = campaignService.save(campaignIn);
		
		if(campaignOut != null)
			return Response.status(Response.Status.OK).entity(campaignOut).build();
		else
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
		
	@POST
	@Path("/deactivate/{POID}/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deactivate(@PathParam("id") int id) 
	{
		int ret = campaignService.deactivate(id);
		
		if(ret == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}

}
