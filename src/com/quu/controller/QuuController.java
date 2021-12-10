package com.quu.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quu.model.Station;
import com.quu.service.IQuuService;
import com.quu.util.Util;


@Path("/")
public class QuuController {

	@Inject
	private IQuuService quuService;
	
	@GET
	@Path("/stations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStations() 
	{
		Map<String, Station> stationMap = quuService.getStations();
		
		List<Station> stationList = new ArrayList<>();
		
		for (Map.Entry<String, Station> entry : stationMap.entrySet()) 
		{
			Station station = entry.getValue();
			stationList.add(station);
		}
		
		return Response.status(Response.Status.OK).entity(stationList).build();
	}
	
	@POST
	@Path("/log")
	@Produces(MediaType.APPLICATION_JSON)
	public Response log(String data)
	{
		Util.logQueryString(data);
		
		return Response.status(Response.Status.OK).build();
	}
	
}
