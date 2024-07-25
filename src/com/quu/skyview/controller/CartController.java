package com.quu.skyview.controller;

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

import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.StationCart;
import com.quu.skyview.service.ICampaignService;
import com.quu.skyview.service.ICartService;


@RequestScoped
@Path("/adlink")
public class CartController { 
	
	@Inject
    private ICartService cartService;
	
	
	@GET
	@Path("/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int stationId) 
	{
		List<StationCart> entity = cartService.getSchedules(stationId);
		
		if(entity != null)
			return Response.status(Response.Status.OK).entity(entity).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Path("/cart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assignStationCartDates(StationCart stationCart)
	{
		int ret = cartService.assignStationCartDates(stationCart);
		
		if(ret > 0)
			return Response.status(Response.Status.OK).build();
		else  //-1
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	@PUT
	@Path("/cart")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStationCartDate(StationCart stationCart)
	{
		int ret = cartService.updateStationCartDate(stationCart);
		
		if(ret > 0)
			return Response.status(Response.Status.OK).build();
		else if(ret == -2)
			return Response.status(Response.Status.NO_CONTENT).build();
		else  //-1
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
}