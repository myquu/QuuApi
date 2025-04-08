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

import com.quu.model.BBCampaign;
import com.quu.model.BBSchedule;
import com.quu.model.BBSchedules;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;
import com.quu.service.IQuuService;
import com.quu.util.Util;


@Path("/")
public class QuuController {

	@Inject
	private IQuuService quuService;
	
	//Gets details of all stations in the database.
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
	
	//Creates billboards including its RT text and schedule from the data Post. There can be multiple BBs in a post but they will all belong to the same station.
	@POST
	@Path("/billboard/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createBillboardsAndDependants(List<BBCampaign> campaignList)
	{
		List<String> status = quuService.createBillboardsAndDependants(campaignList);
		
		if(status.isEmpty())
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.PARTIAL_CONTENT).entity(status).build();
	}
	
	//This is a subset of the above method. Creates schedules for a billboard (whose id is posted) that's already created. There can be multiple schedules each having multiple days, options and stations.
	//Before making this POST the campaign is created from the UI with a schedule (that includes station) and activated. This means there is at least 1 entry for it qrt_station_campaigns_order table.
	@POST
	@Path("/billboardSchedules/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createBillboardSchedules(BBSchedules schedules)
	{
		int status = quuService.createBillboardSchedules(schedules);
		
		if(status == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.PARTIAL_CONTENT).entity(status).build();
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
