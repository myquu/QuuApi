package com.quu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.quu.model.Station;
import com.quu.service.IQuuService;

@Path("/")
//General purpose class for non RESTful entities. NOT IN USE
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
	
}
