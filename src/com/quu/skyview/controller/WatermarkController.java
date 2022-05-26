package com.quu.skyview.controller;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quu.skyview.model.CampaignWatermarks;
import com.quu.skyview.model.Watermark;
import com.quu.skyview.service.IWatermarkService;

@RequestScoped
@Path("/qid")
public class WatermarkController {

	@Inject
    private IWatermarkService watermarkService;
	
	@POST
	@Path("/assign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assign(CampaignWatermarks campaignWatermarks)
	{
		int ret = watermarkService.assign(campaignWatermarks);
		
		if(ret == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Path("/unassign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response unassign(CampaignWatermarks campaignWatermarks)
	{
		int ret = watermarkService.unassign(campaignWatermarks);
		
		if(ret == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@GET
	@Path("/audit")
	@Produces(MediaType.APPLICATION_JSON)
	public Response audit()
	{
		Map<Integer, List<Watermark>> campaignsWatermarks = watermarkService.audit();
		
		if(campaignsWatermarks != null)
			return Response.status(Response.Status.OK).entity(campaignsWatermarks).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}

}