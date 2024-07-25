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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;
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
		StationMaps stationMaps = quuService.getStations();
		
		Map<String, Station> stationMap = stationMaps.getStationCallLettersMap();
		
		List<Station> stationList = new ArrayList<>();
		
		for (Map.Entry<String, Station> entry : stationMap.entrySet()) 
		{
			Station station = entry.getValue();
			stationList.add(station);
		}
		
		return Response.status(Response.Status.OK).entity(stationList).build(); 
	}
	
	//Gets the top 3 logs for every station in the system.
	@GET
	@Path("/RTLogs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRTLogs(@QueryParam("sid") String sid) 
	{
		List<RTLog> log = quuService.getStationRTLogs(sid);
		
		return Response.status(Response.Status.OK).entity(log).build();
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
