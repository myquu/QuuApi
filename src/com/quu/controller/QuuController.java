package com.quu.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quu.model.BBCampaignOut;
import com.quu.model.BBSchedulesIn;
import com.quu.model.BBCampaignsIn;
import com.quu.model.CartAssignment;
import com.quu.model.EmergencyBlastHotkey;
import com.quu.model.RTLog;
import com.quu.model.Station;
import com.quu.model.StationMaps;
import com.quu.model.TimeAvailablePerHour;
import com.quu.model.TimeAvailablePerHourOutput;
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
	
	
	//Creates/Updates billboards including its RT text and schedule from the data Post. There can be multiple BBs in a post.
	@POST
	@Path("/billboard/save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveBillboardsAndDependants(BBCampaignsIn billboardsWithActivateValue)
	{
		List<BBCampaignOut> status = quuService.saveBillboardsAndDependants(billboardsWithActivateValue);
		
		return Response.status(Response.Status.OK).entity(status).build();		
	}
	
	//This is a subset of the above method. Creates schedules for a billboard (whose id is posted) that's already created. There can be multiple schedules each having multiple days, options and stations.
	//Before making this POST the campaign is created from the UI with a schedule (that includes station) and activated. This means there is at least 1 entry for it qrt_station_campaigns_order table.
	@POST
	@Path("/billboardSchedules/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createBillboardSchedules(BBSchedulesIn schedules)
	{
		int status = quuService.createBillboardSchedules(schedules, true);
		
		if(status == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.PARTIAL_CONTENT).entity(status).build();
	}	
	
	@POST
	@Path("/billboard/activate/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activateBillboard(@PathParam("id") int id) 
	{
		int status = quuService.activateBillboard(id);
		
		if(status == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Path("/billboard/deactivate/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deactivateBillboard(@PathParam("id") int id) 
	{
		int status = quuService.deactivateBillboard(id);
		
		if(status == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	
	//Gets the list all emergencyblast campaigns of hotkey type.
	@GET
	@Path("/emergencyblast/hotkeys")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEmergencyblastHotkeys(@QueryParam("stationId") int stationId) 
	{
		List<EmergencyBlastHotkey> list = quuService.getEmergencyblastHotkeys(stationId);
		
		return Response.status(Response.Status.OK).entity(list).build(); 
	}
	
	@POST
	@Path("/emergencyblast/hotkey/activate/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response activateEmergencyblastHotkey(@PathParam("id") int id) 
	{
		int status = quuService.activateEmergencyblastHotkey(id);
		
		if(status == 1)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	
	//Gets the complete Ad sync specific contract type cart change history for the given campaign id and station SID.
	@GET
	@Path("/cartHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public Response cartAssignmentHistory(@QueryParam("campaign_id") int campaignId) 
	{
		List<CartAssignment> detailList = quuService.getCartAssignmentHistory(campaignId);
		
		return Response.status(Response.Status.OK).entity(detailList).build();
	}
	
	
	//https://github.com/QuuInc/Quu2GoV2_QA/issues/236#issuecomment-3184413081
	@GET
	@Path("/availFinder")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTimeAvailablePerHour(@QueryParam("SID") String sid, @QueryParam("date") String date) 
	{
		TimeAvailablePerHourOutput output = quuService.getTimeAvailablePerHour(sid, date);
		
		if(output != null)
			return Response.status(Response.Status.OK).entity(output).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
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
