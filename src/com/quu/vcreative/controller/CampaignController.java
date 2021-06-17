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

import com.quu.util.Constant;
import com.quu.vcreative.model.CampaignIn;
import com.quu.vcreative.model.CampaignOut;
import com.quu.vcreative.model.CampaignStationIn;
import com.quu.vcreative.model.CampaignStationOut;
import com.quu.vcreative.model.ImageIn;
import com.quu.vcreative.model.LineItemIn;
import com.quu.vcreative.model.LineItemOut;
import com.quu.vcreative.service.ICampaignService;


@RequestScoped
@Path("/campaign")
public class CampaignController {
	
	@Inject
    private ICampaignService campaignService;
		
	//Response.Status.NO_CONTENT is not possible here. We create is not found and update if found.
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response save(CampaignIn campaignIn)
	{
		CampaignOut campaignOut = campaignService.save(campaignIn);
		
		List<LineItemOut> lineItemOuts = campaignOut.getLineItems();
		
		if(lineItemOuts != null && !lineItemOuts.isEmpty())
			return Response.status(Response.Status.OK).entity(campaignOut).build();
		else
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	@POST
	@Path("/assignImage")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assignImage(ImageIn imageIn)
	{
		int found = campaignService.assignImage(imageIn);
		
		if(found == 1)
		{
			String previewUrl = Constant.RDSCAMPAIGNPREVIEWURL + imageIn.getId();
			
			return Response.status(Response.Status.OK).entity(new LineItemOut(imageIn.getVC_LineItem_ID(), imageIn.getId(), previewUrl)).build();
		}
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Path("/assign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assignStationsCarts(CampaignStationIn campaignStation)
	{
		String[] ret = campaignService.assignStationsCarts(campaignStation);
		
		CampaignStationOut res = new CampaignStationOut(ret[0], ret[1]); 
		
		return Response.status(Response.Status.OK).entity(res).build();
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
