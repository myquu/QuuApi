package com.quu.network.controller;

import java.io.IOException;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quu.network.model.Schedule;
import com.quu.network.service.IScheduleService;


@RequestScoped
@Path("/schedule")
public class ScheduleController {
	
	@Inject
    private IScheduleService scheduleService;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() 
	{
		List<Schedule> list = scheduleService.getAll();
		
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
		Schedule entity = scheduleService.get(id);
		
		if(entity != null)
			return Response.status(Response.Status.OK).entity(entity).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response add(String jsonS)
	{
		int id = -1;
		
		//The sttring is the json. Pass it as is + parse and pass whaetever othre fields are needed
		try {
			JsonNode root = mapper.readTree(jsonS);
			
			JsonNode Value = root.path("Value"); 
						
			int StationID = Value.path("StationID").asInt();
						
			String CampaignIDs = "";
			
			JsonNode EventsNode = Value.path("Events");
            if (EventsNode.isArray()) 
            {
            	for (JsonNode node1 : EventsNode) 
            	{
                    JsonNode BreaksNode = node1.path("Breaks");
                    if (BreaksNode.isArray()) 
                    {
                    	for (JsonNode node2 : BreaksNode) 
                    	{
                    		JsonNode AdvertisementsNode = node2.path("Advertisements");
                            if (AdvertisementsNode.isArray()) 
                            {
                            	for (JsonNode node3 : AdvertisementsNode) 
                            	{
                            		String CampaignID = node3.path("CampaignID").asText();
                            		CampaignIDs += CampaignID + ","; 
                            	}
                            }
                    	}
                    }
                }
            }
            
            //Delete the last comma 
            if(!CampaignIDs.isEmpty())
            {
            	CampaignIDs = CampaignIDs.substring(0, CampaignIDs.length()-1);
            }
            
			id = scheduleService.add(jsonS, StationID, CampaignIDs);
		}
		catch (JsonProcessingException e) {
            e.printStackTrace();
        } 
		
		return Response.status(Response.Status.OK).entity("{\"ID\":"+id+"}").build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(String jsonS)
	{
		int id = -1;
		int rowUpdated = -1;
		
		try {
			JsonNode root = mapper.readTree(jsonS);
			
			id = root.path("ID").asInt();
			JsonNode Value = root.path("Value");
			
			int StationID = Value.path("StationID").asInt();
			
			String CampaignIDs = "";
			
			JsonNode EventsNode = Value.path("Events");
            if (EventsNode.isArray()) 
            {
            	for (JsonNode node1 : EventsNode) 
            	{
                    JsonNode BreaksNode = node1.path("Breaks");
                    if (BreaksNode.isArray()) 
                    {
                    	for (JsonNode node2 : BreaksNode) 
                    	{
                    		JsonNode AdvertisementsNode = node2.path("Advertisements");
                            if (AdvertisementsNode.isArray()) 
                            {
                            	for (JsonNode node3 : AdvertisementsNode) 
                            	{
                            		String CampaignID = node3.path("CampaignID").asText();
                            		CampaignIDs += CampaignID + ","; 
                            	}
                            }
                    	}
                    }
                }
            }
            
            //Delete the last comma 
            if(!CampaignIDs.isEmpty())
            {
            	CampaignIDs = CampaignIDs.substring(0, CampaignIDs.length()-1);
            }
            
            rowUpdated = scheduleService.update(id, jsonS, StationID, CampaignIDs);
		}
		catch (JsonProcessingException e) {
            e.printStackTrace();
        }
				
		if(rowUpdated > 0)
			return Response.status(Response.Status.OK).entity("{\"ID\":"+id+"}").build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
	@DELETE
	@Path("/{id: \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) 
	{
		int count = scheduleService.delete(id);
		
		if(count > 0)
			return Response.status(Response.Status.OK).build();
		else
			return Response.status(Response.Status.NO_CONTENT).build();
	}
	
}
