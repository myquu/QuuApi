package com.quu.skyview.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quu.dao.IQuuDAO;
import com.quu.model.Station;
import com.quu.model.StationInput;
import com.quu.service.AblyUtil;
import com.quu.skyview.dao.IScheduleDAO;
import com.quu.skyview.model.Campaign;
import com.quu.skyview.model.Trigger;
import com.quu.util.Scheduler;
import com.quu.util.Util;


@RequestScoped
public class TriggerService implements ITriggerService{

	@Inject
    private IScheduleDAO scheduleDAO;
		
	/*
    //@Override
    public int sendCampaigns(String json) {
        
    	try
    	{
	    	JSONObject obj = new JSONObject(json);
			
			int campaignId = obj.optInt("CampaignID");  //Will give 0 if CampaignID is null
			//JSONObject js = obj.optJSONObject("js");
	    	
			if(campaignId > 0)
			{
		    	Map<String, Station> stationMap = Scheduler.StationMap;
		    	
		    	String callLetters = "KRAP-AM";
		    	
		    	Station station = stationMap.get(callLetters);
		    	
		    	if(station != null)
		    	{
			    	List<StationInput> inputList = station.getInputList();
			    	
			    	for(StationInput input : inputList)
			    	{
			    		if(input.getType().equals("Network"))
			    		{
			    			Campaign campaign = campaignDAO.get(campaignId);
			    	    	
			    	    	JSONObject objE = createEvent(campaign, 30000);
			    	    	
			    	    	//input.getIp() will be the external IP for Network type. "170.39.241.151"
			    	    	Util.sendToTCPServer(input.getPort(), input.getIp(), objE.toString());
			    	    	//System.out.println("TriggerService 333: " + objE.toString());
			    	    	return 1;
			    		}
			    	}
		    	}
			}
    	}
    	catch(JSONException ex) {
    		return -1;
		}
    	
    	return -1;
    }
    */
    
    @Override
    /**
     * This method sends the trigger to all Quu360 instances running those stations that are linked to the passed eventId
     */
	public int process(Trigger trigger) 
    {
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	String data = null;
    	
    	//Deserialize to read the eventId
    	try {
    		data = objectMapper.writeValueAsString(trigger);
    	}
    	catch(JsonProcessingException ex) {}
    	
    	List<Integer> stationIdList = scheduleDAO.getStationsForEventId(trigger.getEventId());
    	
    	if(stationIdList != null)
    	{
    		Map<Integer, Station> stationIdMap = Scheduler.stationMaps.getStationIdMap();
    		String dataF = data;
    		    		
    		for(int stationId : stationIdList)
    		{
    			Station station = stationIdMap.get(stationId);
    			
    			if(station != null)
    			{
	    			String stationGroupCode = station.getGroupCode(); 
	    			//new Thread(() -> AblyUtil.publish("Skyview", "NetworkTrigger:"+stationId, dataF)).start();
	    			new Thread(() -> AblyUtil.publish("Skyview", stationGroupCode, dataF)).start();
    			}
    		}
    	}
    	
		return 1;
	}




	//Rivendell format
    //The only fields that matter are cart and category(groupName).
    private JSONObject createEvent(Campaign campaign, int duration)
    {
    	JSONObject obj = new JSONObject();
    	
    	try {
    		JSONObject nowObj = new JSONObject();
    		nowObj.put("cartNumber", campaign.getId());
	    	nowObj.put("groupName", "COM");
	    	nowObj.put("artist", campaign.getLine1());
	    	nowObj.put("title", campaign.getLine2());
	    	nowObj.put("length", duration);
	    	
	    	JSONObject padUpdateObj = new JSONObject();
	    	padUpdateObj.put("now", nowObj);
	    	obj.put("padUpdate", padUpdateObj);
    	}
    	catch(JSONException ex) {}
    	
    	return obj;
    }
    
}
